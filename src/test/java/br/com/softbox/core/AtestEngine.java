package br.com.softbox.core;

import br.com.softbox.utils.*;
import com.google.common.eventbus.AsyncEventBus;
import com.sun.jna.platform.FileUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class AtestEngine implements TestController {
    private AsyncEventBus _eventBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
    private LinkedHashMap<String, TestData> _tests = new LinkedHashMap<>();
    private AtestEvidence _evidence = AtestEvidence.getInstance();
    private AtestSettingsLoader _settings = null;
    private volatile RunningType _isRunningType = RunningType.NONE;
    private PrintWriter _traceWriter = null;
    private boolean _initiatedSettings = false;
    private boolean _initiatedBDDs = false;
    private boolean _initiatedSelectors = false;
    private BDDSettingsLoader _settingsBDD = null;
    private String _traceCoreFileName;
    private final String _givenIamAt = "Given I am at ";
    private AtestWebDriver _webDriverMgr = null;
    private volatile String _testNameInExecution = "";
    private volatile boolean _stop = false;
    private AtestLog _log = new AtestLog("AtestEngine");
    private String _store;
    private String storeDir;


    public AtestEngine() {
        createTraceCoreFile();
        _evidence.setEngine(this);
    }

    private void createTraceCoreFile() {
        if (_traceWriter == null) {
            _traceCoreFileName = "atest_core_" + Utils.getCurDateTimeForFileNames() + ".log";
            try {
                _traceWriter = new PrintWriter(_traceCoreFileName, "UTF-8");
            } catch (FileNotFoundException e) {
                _log.oops("ERROR: attempt to create trace core file [" + _traceCoreFileName + "] : " + e.getMessage());
            } catch (UnsupportedEncodingException e) {
                _log.oops("ERROR: Unsupported encoded UTF-8 for trace core file [" + _traceCoreFileName + "] : " + e.getMessage());
            }
        }
    }

    /**
     */
    public String init() {
        loadSettings();
        loadBDDs();
        loadSelectors();
        return (_initiatedSettings && _initiatedBDDs && _initiatedSelectors) ? "" : _traceCoreFileName;
    }

    public void shutdown() {
        if (_traceWriter != null) {
            _traceWriter.close();
        }
        if (_webDriverMgr != null) {
            _webDriverMgr.quit();
        }
    }

    private void loadSelectors() {
        try {
            AtestSelectorLoader selectors = new AtestSelectorLoader();
            AtestWebElement.getInstance().setSelectors(selectors.getEntry());
            traceCore("Loaded selectors from resource [" + selectors.getResourcePath() + "]");
            _initiatedSelectors = true;
            // TODO: validar os seletores (indicar se sao usados no codigo ou nao)

        } catch (AtestCoreException e) {
            traceCore("EXCEPTION: loadSelectors: " + e.getMessage());
        } catch (Exception e) {
            traceCore("EXCEPTION: loadSelectors: Please, check the json file. / " + e.getMessage());
        }
    }

    private void loadBDDs() {
        try {
            _settingsBDD = new BDDSettingsLoader();
            traceCore("Loaded BDDs from resource [" + _settingsBDD.getResourcePath() + "]");
            _settingsBDD.getBDDs().forEach(bdd -> {
                traceCore("    ----------------------------------------------------");
                traceCore("    title...: " + bdd.title);
                traceCore("    pkg.....: " + bdd.pkg);
                traceCore("    feature.: " + bdd.feature);
                traceCore("    test....: " + bdd.test);
                traceCore("    stepdefs: " + bdd.stepdefs);
            });
            initTestsData();
            _initiatedBDDs = true;
        } catch (AtestCoreException e) {
            traceCore("EXCEPTION: loadBDDs: " + e.getMessage());
        } catch (Exception e) {
            traceCore("EXCEPTION: loadBDDs: Please, check for missing commas. Is there really ["
                    + _settingsBDD.getBDDs().size() + "] entries in this file? / " + e.getMessage());
        }
    }

    private void loadSettings() {
        try {
            _settings = new AtestSettingsLoader();
            traceCore(String.format("Loaded settings from resource [%s]", _settings.getResourcePath()));
            traceCore("    root directory....: " + _settings.getRootDir());
            traceCore("    work directory....: " + _settings.getRawWorkspaceDir());
            traceCore("    evidence directory: " + _settings.getRawEvidenceDir());
            traceCore("    feature directory.: " + _settings.getRawFeatureDir());
            traceCore("    sites.............: ");
            _store = _settings.getStores().get(0);      // Default value.
            for (String store : _settings.getStores()) {
                traceCore("         (" + store + ")");
                _settings.getSites(store).forEach(site -> traceCore("            * " + site));
            }
            moveFeatures();
            _webDriverMgr = new AtestWebDriver(_settings);
            _initiatedSettings = true;
        } catch (AtestCoreException e) {
            traceCore("EXCEPTION: loadSettings: " + e.getMessage());
        } catch (Exception e) {
            traceCore("EXCEPTION: loadSettings: Please, check the json file. / " + e.getMessage());
        }
    }

    private void traceCore(String msg) {
        _traceWriter.println(msg);
        _traceWriter.flush();
    }

    public List<String> getSettingsSites() {
        return _settings.getSites(_store);
    }

    private void setRunningType(RunningType type) {
        _isRunningType = type;
        _eventBus.post(type);
    }

    @Override
    public void runAll(WebBrowser browser) {
        configureWebBrowser(browser);
        final String evidenceDir = _evidence.beginBundle("smoke");
        if (!evidenceDir.isEmpty()) {
            _log.info("Evidence for SMOKE TEST at: " + evidenceDir);
            Runnable task = () -> {
                setRunningType(RunningType.SMOKE);
                for (String testName : _tests.keySet()) {
                    if (_stop) {
                        _testNameInExecution = "";
                        _log.info("Stop requested by the user!");
                        _stop = false;
                        break;
                    }
                    if (_tests.get(testName).enabled) {
                        _log.info("Starting test [" + testName + "] ...");
                        try {
                            _testNameInExecution = testName;
                            runTest(testName);
                            _testNameInExecution = "";
                        } catch (AtestExecutionException e) {
                            _log.oops(e.getMessage());
                            _testNameInExecution = "";
                        }
                    } else {
                        _log.info("Skipping test [" + testName + "] (DISABLED)");
                    }
                }
                setRunningType(RunningType.NONE);
                _evidence.endBundle();
                _webDriverMgr.quit();
            };
            Thread thread = new Thread(task);
            thread.setName("THREAD-test[smoke]");
            thread.setDaemon(true);
            thread.start();
        } else {
            _log.oops("Unable to create the evidence directory for test [smoke]");
        }
    }

    /**
     * Runs a single test.
     *
     * @param testTitle The test's title
     * @throws AtestExecutionException
     */
    private void runTest(String testTitle) throws AtestExecutionException {
        final String evidenceDir = _evidence.beginTestSession(testTitle);
        if (evidenceDir.isEmpty()) {
            _log.oops("Could not create the evidence diretory for [" + testTitle + "]");
            return;
        }
        _log.info("Evidence at: " + evidenceDir);
        TestData test = _tests.get(testTitle);
        test.status = TestData.TestStatus.TEST_RUNNING;
        _eventBus.post(test);
        _evidence.trace(test);
        final String className = test.bddPkg + "." + test.bddTest;
        try {
            test.startTime = Utils.getTime();
            Result r = JUnitCore.runClasses(Class.forName(String.valueOf(className)));
            test.endTime = Utils.getTime();
            if (r.wasSuccessful()) {
                test.junitRuntime = r.getRunTime();
                test.status = TestData.TestStatus.TEST_PASSED;
            } else {
                final String failures = getJUnitFailuresAsString(r.getFailures());
                test.status = failures.contains("initializationError")
                        ? TestData.TestStatus.TEST_ERROR_CUCUMBER_LOADING : TestData.TestStatus.TEST_FAILED;
                test.errorAfterJUnit = failures;
                test.errorOnCurrentURL = _evidence.getWebDriver().getCurrentUrl();
            }
            test.report = new AtestCucumberReport(testTitle, _settings.getEvidenceDir()).getReport();
            _eventBus.post(test);
            _evidence.trace(test);
            _evidence.endTestSession(testTitle);
        } catch (ClassNotFoundException e) {
            test.status = TestData.TestStatus.TEST_ERROR_CLASS_LOADING;
            final String err = "EXCEPTION: loading class [" + className + "]. ";
            _eventBus.post(test);
            _evidence.endTestSession(testTitle);
            throw new AtestExecutionException(err);
        } catch (AtestWebDriverException e) {
            ;
        }
    }

    private String getJUnitFailuresAsString(List<Failure> failures) {
        String errors = "";
        for (Failure f : failures) {
            errors += f.getDescription().getDisplayName() + " | "
                    + f.getException().getMessage() + " | ";
        }
        return errors;
    }

    private String getFailuresAsString(List<Failure> failures) {
        String msg = "";
        failures.forEach(failure -> {
            // msg += failure.
        });
        return msg;
    }

    @Override
    public String getRootDir() {
        return _settings.getRootDir();
    }

    @Override
    public String getRawWorkspaceDir() {
        return _settings.getRawWorkspaceDir();
    }

    @Override
    public String getRawFeatureDir() {
        return _settings.getRawFeatureDir();
    }

    @Override
    public String getRawEvidenceDir() {
        return _settings.getRawEvidenceDir();
    }

    /**
     * Run a test.
     * The method "isEnabled()" should be called before calling this one.
     *
     * @param testName The test's title
     */
    @Override
    public void run(String testName, WebBrowser browser) {
        configureWebBrowser(browser);
        Runnable task = () -> {
            final String evidenceBundleDir = _evidence.beginBundle("single");
            if (evidenceBundleDir.isEmpty()) {
                _log.oops("Unable to create the evidence directory for test [" + testName + "]");
                return;
            }
            setRunningType(RunningType.SINGLE);
            _log.info("Starting test [" + testName + "] ...");
            try {
                _testNameInExecution = testName;
                runTest(testName);
            } catch (AtestExecutionException e) {
                _log.oops(e.getMessage());
            }
            _testNameInExecution = "";
            setRunningType(RunningType.NONE);
            _webDriverMgr.quit();
            _evidence.endBundle();
        };
        Thread thread = new Thread(task);
        thread.setName("THREAD-test[" + testName + "]");
        thread.setDaemon(true);
        thread.start();
    }

    private void configureWebBrowser(WebBrowser browser) {
        if (_isRunningType == RunningType.NONE) {
            try {
                if (browser == WebBrowser.FIREFOX) {
                    _evidence.setWebDriver(_webDriverMgr.firefox());
                    _evidence.setBrowserInUse(WebBrowser.FIREFOX, TestController.FIREFOX);
                } else if (browser == WebBrowser.CHROME) {
                    _evidence.setWebDriver(_webDriverMgr.chrome());
                    _evidence.setBrowserInUse(WebBrowser.CHROME, TestController.CHROME);
                } else if (browser == WebBrowser.IE) {
                    _evidence.setWebDriver(_webDriverMgr.ie());
                    _evidence.setBrowserInUse(WebBrowser.IE, TestController.IE);
                }
                _evidence.getWebDriver().manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            } catch (AtestWebDriverException e) {
                _log.oops("EXCEPTION: configureWebBrowser(): " + e.getMessage());
            } catch (AtestExecutionException e) {
                _log.oops(e.getMessage());
            }
        }
    }

    @Override
    public void stop(String testName) {
        if (_isRunningType == RunningType.SINGLE && _testNameInExecution.equals(testName)) {
            _webDriverMgr.shutdown();
        }
    }

    @Override
    public void stopAll() {
        if (_isRunningType == RunningType.SMOKE) {
            _stop = true;
            _webDriverMgr.shutdown();
        }
    }

    /**
     * Changes the test availability to ENABLED or DISABLED
     *
     * @param testName The test's title
     * @throws AtestExecutionException In case of an unknown test title.
     */
    @Override
    public boolean toggle(String testName) throws AtestExecutionException {
        if (!_tests.containsKey(testName)) {
            throw new AtestExecutionException("Unknown test[" + testName + "]");
        }
        TestData t = _tests.get(testName);
        t.enabled = !t.enabled;
        _eventBus.post(t);
        return t.enabled;
    }

    @Override
    public LinkedHashMap<String, TestData> getTests() {
        return _tests;
    }

    @Override
    public void changeInitialSite(String newSite) {
        for (Map.Entry<String, TestData> entry : _tests.entrySet()) {
            TestData td = entry.getValue();
            String _givenIamAtRegex = "Given\\s+I\\s+am\\s+at\\s+\"(.*)\"";
            td.featureContents = td.featureContents.replaceAll(_givenIamAtRegex, _givenIamAt + "\"" + newSite + "\"");
            saveFeature(td);
        }
    }

    @Override
    public List<String> getAvailableHosts() {
        return _settings.getSites(_store);
    }

    @Override
    public List<WebBrowser> getAvailableWebBrowsers() {
        return _webDriverMgr.getAvailableWebBrowsers();
    }

    @Override
    public void saveFeatureChangings(String testName, String newFeatureContents) {
        if (_tests.containsKey(testName)) {
            TestData td = _tests.get(testName);
            td.featureContents = newFeatureContents;
            saveFeature(td);
            _eventBus.post(td);
        } else {
            _log.oops("saveFeatureChangings: Unknown test [" + testName + "]");
        }
    }

    @Override
    public boolean isRunning() {
        return _isRunningType != RunningType.NONE;
    }

    @Override
    public boolean isWebDriverShutdown() {
        return _webDriverMgr.isShutdown();
    }

    @Override
    public String getTraceFileContents(String testName) {
        return _evidence.getTraceFileContents(testName);
    }

    @Override
    public String getTestNameInExecution() {
        return _testNameInExecution;
    }

    @Override
    public void setEvidenceLevel(EvidenceLevel level) {
        _evidence.setLevel(level);
    }

    @Override
    public void postScenario(TestScenarioData sc) {
        _eventBus.post(sc);
    }

    @Override
    public void postScenarioStep(TestStepData step) {
        _eventBus.post(step);
    }

    @Override
    public boolean isBrowserIntanceOfInternetExplorer() throws AtestExecutionException {
        return _webDriverMgr.isIE();
    }

    @Override
    public HashMap<MainMenuOptionCode, String> getMainMenuUrlOptions() {
        return _settings.getMainMenuUrlOptions(_evidence.getInitialSiteURL());
    }

    public void setListener(Object obj) {
        _eventBus.register(obj);
    }

    private void initTestsData() {
        _settingsBDD.getBDDs().forEach(bdd -> {
            TestData t = new TestData();
            t.testName = bdd.title;
            t.featureFilename = bdd.feature;
            t.stepdefsFilename = bdd.stepdefs;
            t.bddPkg = bdd.pkg;
            t.bddTest = bdd.test;
            t.status = TestData.TestStatus.TEST_READY_TO_RUN;
            t.featureContents = getFeatureContents(bdd.feature);
            t.enabled = true;
            _tests.put(t.testName, t);
        });
    }

    private String getFeatureFilePath(String featureFileName) {
        return _settings.getFeatureDir()
                + File.separator
                + featureFileName
                + ".feature";
    }

    private String getFeatureContents(String featureFileName) {
        try {
            final String path = getFeatureFilePath(featureFileName);
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, Charset.defaultCharset()).replaceAll("\\r", "");
        } catch (IOException e) {
            _log.oops("EXCEPTION: unabled to read feature file [" + featureFileName + "]. " + e.getMessage());
        }
        return "";
    }

    public void postEvidence(AtestLogEvent event) {
        _eventBus.post(event);
    }

    public String getEvidenceDir() {
        return _settings.getEvidenceDir() + File.separator + _store;
    }

    /**
     * Save the feature file changes.
     */
    private void saveFeature(TestData td) {
        final String path = getFeatureFilePath(td.featureFilename);
        // Erases the file contents.
        try {
            FileWriter f = new FileWriter(path);
            f.write("");
            f.append(td.featureContents);
            f.close();
        } catch (IOException e) {
            _log.oops(e.getMessage());
        }
    }

    @Override
    public List<String> getStores() {
        return _settings.getStores();
    }

    @Override
    public void setStore(String store, boolean backup) {
        if (backup && !_store.equals(store)) {
            backupFeatures();
        }
        _store = store;
        if (_isRunningType == RunningType.NONE && moveFeatures()) {
            initTestsData();
        }
    }

    private void backupFeatures() {
        File dirOrig = new File(_settings.getFeatureDir());
        File dirDest = null;
        if (_store.equals(_settings.getStore_1())) {
            dirDest = new File(_settings.getFeatureDirStore_1());
        } else if (_store.equals(_settings.getStore_2())) {
            dirDest = new File(_settings.getFeatureDirStore_2());
        }
        try {
            org.apache.commons.io.FileUtils.copyDirectory(dirOrig, dirDest);
        } catch (IOException e) {
            _log.oops("Could not backup feature files to its destiny [" + dirDest.toString() + "] directory");
        }
    }

    private boolean moveFeatures() {
        File dirDest = new File(_settings.getFeatureDir());
        File dirOrig = null;
        if (_store.equals(_settings.getStore_1())) {
            dirOrig = new File(_settings.getFeatureDirStore_1());
        } else if (_store.equals(_settings.getStore_2())) {
            dirOrig = new File(_settings.getFeatureDirStore_2());
        }
        try {
            org.apache.commons.io.FileUtils.copyDirectory(dirOrig, dirDest);
            return true;
        } catch (IOException e) {
            _log.oops("Could not move feature files from [" + _store + "] to its destiny 'feature' directory");
        }
        return false;
    }
}
