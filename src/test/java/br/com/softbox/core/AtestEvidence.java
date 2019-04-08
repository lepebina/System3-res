package br.com.softbox.core;

import br.com.softbox.utils.*;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class AtestEvidence {
    private final String NAME_SEPARATOR = "_";
    private final String DIRNAME_SUFFIX_DEFAULT = "evidence";
    private String _evidenceDir = "";
    private final int FIRST_SUFFIX_DIR_ID = 1;
    private final String _fourDigitPathSuffix = "_\\d{4}";
    private final String _fourDigitPathPrefix = "\\d{4}_";
    private String _bundleRootPath = "";
    private String _testInExecution = "";
    private String _testInExecutionDir = "";
    private String _traceFileName = "";
    private String _traceFilePath = "";
    private PrintWriter _traceWriter = null;
    private String _bundleNamePrefix = DIRNAME_SUFFIX_DEFAULT;
    private static AtestEvidence _instance;
    private HashMap<String, String> _traceFilePathHistory = new HashMap<String, String>();
    private WebDriver _webdriver = null;
    private TestController.WebBrowser _browserInUse = TestController.WebBrowser.NONE;
    private String _browserNameInUse = "";
    private String _currentHost = "";
    private AtestEngine _engine = null;
    private TestController.EvidenceLevel _level = TestController.EvidenceLevel.ON_ERROR;
    private String _lastTestInExecutionDir = "";
    private String _initialSiteURL;
    private TestStepData _currentScenarioStep = null;
    private TestScenarioData _currentScenario = null;

    static {
        _instance = new AtestEvidence();
    }


    /**
     * Get the AtestEvidence instance.
     *
     * @return the AtestEvidence instance.
     */
    public static AtestEvidence getInstance() {
        return _instance;
    }

    public void setWebDriver(WebDriver wb) {
        _webdriver = wb;
    }

    /**
     * Returns the webdriver
     *
     * @return the webdriver
     */
    public WebDriver getWebDriver() throws AtestWebDriverException {
//        if (_engine.isWebDriverShutdown()) {
//            throw new AtestWebDriverException("Shutdown in progress");
//        }
        return _webdriver;
    }

    /**
     * Sets the evidence directory. This information comes from the settings.json.
     */
    private void setEvidenceDir() {
        if (_evidenceDir.isEmpty()) {
            _evidenceDir = _engine.getEvidenceDir();
        }
    }

    public boolean isIE() throws AtestWebDriverException, AtestExecutionException {
        return _engine.isBrowserIntanceOfInternetExplorer();
    }

    /**
     * Returns the absolute path for the place where the evidences will be saved.
     *
     * @param id           The number of next bundle (directory) prefix
     * @param bundlePrefix The prefix directory name
     */
    private String getBundlePath(int id, String bundlePrefix) {
        return _evidenceDir + File.separator + bundlePrefix + NAME_SEPARATOR + String.format("%04d", id);
    }

    /**
     * Get the next id for the bundle.
     */
    private int getNextBundleDirId(String rootDirName) {
        File ed = new File(rootDirName);
        String[] items = ed.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                if (new File(current, name).isDirectory()) {
                    return name.matches(_bundleNamePrefix + _fourDigitPathSuffix);
                }
                return false;
            }
        });
        int id = FIRST_SUFFIX_DIR_ID;
        if (items != null && items.length > 0) {
            // Example
            // [0] -> smoke_0001
            // [1] -> smoke_0002
            // [2] -> smoke_0003
            Arrays.sort(items, Collections.reverseOrder());
            String lastItem = items[0];
            id = Integer.parseInt(lastItem.substring(lastItem.lastIndexOf(NAME_SEPARATOR) + 1)) + 1;
        }
        return id;
    }

    /**
     * Creates the root bundle path.
     *
     * @param bundleName The directory prefix.
     */
    private void createBundleRootPath(String bundleName) {
        if (bundleName.isEmpty()) {
            _bundleNamePrefix = DIRNAME_SUFFIX_DEFAULT;
        } else {
            _bundleNamePrefix = bundleName;
        }
        int id = getNextBundleDirId(_evidenceDir);
        _bundleRootPath = getBundlePath(id, _bundleNamePrefix);
    }

    /**
     * Creates a directory.
     *
     * @param dirname
     */
    private boolean createDirectory(String dirname) {
        boolean retCode = true;
        File newDir = new File(dirname);
        try {
            newDir.mkdir();
        } catch (SecurityException e) {
            System.out.println(e);
            retCode = false;
            _bundleRootPath = "";
        }
        return retCode;
    }

    /**
     * Initializes the directory where the evidences will be saved.
     */
    public String beginBundle(String bundleName) {
        setEvidenceDir();
        createBundleRootPath(bundleName);
        _bundleRootPath.replace(' ', '_');
        createDirectory(_bundleRootPath);
        return _bundleRootPath;
    }

    public String getCurrentBundlePath() {
        return _bundleRootPath;
    }

    public String getLastLastTestSessionDir() {
        return _lastTestInExecutionDir;
    }

    /**
     * Finalize the current directory of evidences.
     */
    public void endBundle() {
        _bundleRootPath = "";
        if (_webdriver != null && _webdriver.toString() != null) {
            _webdriver.quit();
        }
    }

    /**
     * Returns the next ID for a test session directory.
     * Example:  0012_Out_Of_stock/
     *
     * @return
     */
    private int getNextTestSessionDirId(String rootDirName) {
        String pat = _fourDigitPathPrefix + ".*";
        File ed = new File(rootDirName);
        String[] items = ed.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                if (new File(current, name).isDirectory()) {
                    return name.matches(pat);
                }
                return false;
            }
        });
        int id = FIRST_SUFFIX_DIR_ID;
        if (items != null && items.length > 0) {
            // Example
            // [0] -> 0003_out_of_stock
            // [1] -> 0002_pay_with_card
            Arrays.sort(items, Collections.reverseOrder());
            String lastItem = items[0];
            id = Integer.parseInt(lastItem.substring(0, lastItem.indexOf(NAME_SEPARATOR))) + 1;
        }
        return id;
    }

    /**
     * Creates the trace file for the current test in execution.
     */
    private void createTraceFile() {
        if (_traceWriter == null) {
            _traceFileName = "trace_" + Utils.getCurDateTimeForFileNames() + ".txt";
            _traceFilePath = _testInExecutionDir + File.separator + _traceFileName;
            String testTile = _testInExecution;
            _traceFilePathHistory.put(testTile.replace('_', ' '), _traceFilePath);
            try {
                _traceWriter = new PrintWriter(_traceFilePath, "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                _traceWriter = null;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                _traceWriter = null;
            }
        }
    }

    /**
     * Begins a test session. Will create a evidence directory for the test in execution.
     *
     * @param testTitle
     */
    public String beginTestSession(String testTitle) {
        _testInExecution = testTitle.replace(' ', '_');
        int id = getNextTestSessionDirId(_bundleRootPath);
        _testInExecutionDir = _bundleRootPath
                + File.separator
                + String.format("%04d", id)
                + NAME_SEPARATOR
                + _browserNameInUse
                + NAME_SEPARATOR
                + _testInExecution;
        createDirectory(_testInExecutionDir);
        createTraceFile();
        _lastTestInExecutionDir = _testInExecutionDir;
        return _testInExecutionDir;
    }

    /**
     * Finalizes a test session.
     *
     * @param testTitle
     */
    public void endTestSession(String testTitle) {
        _testInExecution = "";
        _testInExecutionDir = "";
        if (_traceWriter != null) {
            _traceWriter.close();
            _traceWriter = null;
        }
        _traceFilePath = "";
        _traceFileName = "";
        _currentHost = "";
    }

    /**
     * Put a message inside a trace file.
     *
     * @param s
     */
    public void trace(String s) {
        if (_traceWriter != null) {
            _traceWriter.println(s);
            _traceWriter.flush();
        }
    }

    /**
     * Reads the directory to figure out which will be the next file prefix id.
     *
     * @param rootDirName The base directory where the survey should be done.
     */
    public int getNextFilePrefixId(String rootDirName) {
        String pat = _fourDigitPathPrefix + ".*\\..*";
        File ed = new File(rootDirName);
        String[] items = ed.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return name.matches(pat);
            }
        });
        int id = FIRST_SUFFIX_DIR_ID;
        if (items != null && items.length > 0) {
            Arrays.sort(items, Collections.reverseOrder());
            String lastItem = items[0];
            id = Integer.parseInt(lastItem.substring(0, lastItem.indexOf(NAME_SEPARATOR))) + 1;
        }
        return id;
    }

    private void doScreenshot(String title) {
        final String pictureName = Utils.replacePunctuation(title, "_");
        if (_webdriver == null || _webdriver.toString() == null) {
            trace("ERROR: NO WEBDRIVER INSTANCE WAS CONFIGURED");
            return;
        }
        try {
            File sf = ((TakesScreenshot) _webdriver).getScreenshotAs(OutputType.FILE);
            int id = getNextFilePrefixId(_testInExecutionDir);
            String filePath = _testInExecutionDir
                    + File.separator
                    + String.format("%04d", id)
                    + NAME_SEPARATOR
                    + pictureName
                    + ".png";
            FileUtils.copyFile(sf, new File(filePath));
        } catch (IOException e) {
            trace("EXCEPTION: screenshot(): [" + pictureName + "] " + e.getMessage());
        }
    }

    public void screenshotAtEnd(String title) {
        if (_level == TestController.EvidenceLevel.AT_THE_END || _level == TestController.EvidenceLevel.FULL) {
            doScreenshot("TEST_FINISHED_" + title);
        }
    }

    public void screenshotOnError(String title) {
        if (_level == TestController.EvidenceLevel.ON_ERROR
                || _level == TestController.EvidenceLevel.FULL) {
            doScreenshot("ERROR_" + title);
        }
    }

    /**
     * Takes a screen shot.
     */
    public void screenshot(String title) {
        if (_level == TestController.EvidenceLevel.FULL) {
            doScreenshot(title);
        }
    }

    /**
     * Returns the trace file contents of the current test in execution.
     */
    public String getTraceFileContents(String testTitle) {
        String contents = new String();
        if (_traceFilePathHistory.containsKey(testTitle)) {
            try {
                byte[] encoded = Files.readAllBytes(Paths.get(_traceFilePathHistory.get(testTitle)));
                contents = new String(encoded, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return contents;
    }

    public void setCurrentHost(String url) {
        _currentHost = url;
    }

    public String getCurrentHost() {
        return _currentHost;
    }

    public void setEngine(AtestEngine atestEngine) {
        _engine = atestEngine;
    }

    public void log(AtestLogEvent event) {
        System.out.println("THE _engine VALUE IS: "+ _engine);
       // _engine.postEvidence(event);
        trace(Utils.formatLogEvent(event));
    }

    public void trace(TestData test) {
        final String s =
                "\n-------TEST EVENT ------\n"
                        + "          name..........: " + test.testName + "\n"
                        + "          enabled.......: " + String.valueOf(test.enabled) + "\n"
                        + "          status........: " + getTestStatusName(test.status) + "\n"
                        + "          start time....: " + test.startTime + "\n"
                        + "          end time......: " + test.endTime + "\n"
                        + "          feature.......: " + test.featureFilename + "\n"
                        + "          step defs.....: " + test.stepdefsFilename + "\n"
                        + "          bdd pkg.......: " + test.bddPkg + "\n"
                        + "          bdd test......: " + test.bddTest + "\n";
        trace(s);
    }

    public String getTestStatusName(TestData.TestStatus status) {
        if (status == TestData.TestStatus.TEST_READY_TO_RUN) return "READY TO RUN";
        if (status == TestData.TestStatus.TEST_ERROR_CLASS_LOADING) return "ERROR CLASS LOADING";
        if (status == TestData.TestStatus.TEST_FAILED) return "FAILED";
        if (status == TestData.TestStatus.TEST_PASSED) return "PASSED";
        return "";
    }

    public void trace(AtestLogEvent logEvent) {
        trace(Utils.formatLogEvent(logEvent));
    }

    public void setLevel(TestController.EvidenceLevel level) {
        _level = level;
    }

    public void setBrowserInUse(TestController.WebBrowser browser, String browserName) {
        _browserNameInUse = browserName;
        _browserInUse = browser;
    }

    public TestController.WebBrowser getBrowserInUse() {
        return _browserInUse;
    }

    public void setInitialSiteURL(String url) {
        _initialSiteURL = url;
    }

    public String getInitialSiteURL() {
        return _initialSiteURL;
    }

    public String getMainMenuPageURL(TestController.MainMenuOptionCode optionCode) {
        HashMap<TestController.MainMenuOptionCode, String> options = _engine.getMainMenuUrlOptions();
        String url = "";
        if (options.containsKey(optionCode)) {
            url = options.get(optionCode);
        }
        return url;
    }

    public boolean getCurrentPage(TestController.MainMenuOptionCode optionCode) {
        return _webdriver.getCurrentUrl().contains(getMainMenuPageURL(optionCode));
    }

    public void notifyScenario(TestScenarioData sc) {
        //_engine.postScenario(sc);
    }

    public void startScenario(AtestLog log, String scenarioName) {
        AtestLogEvent logEvent = new AtestLogEvent(AtestLogEvent.LogType.INFO
                , log.me() + " SCENARIO", scenarioName);
        log(logEvent);

        TestScenarioData sc = new TestScenarioData();
        sc.testName = _testInExecution.replace("_", " ");
        sc.name = scenarioName;
        sc.status = TestScenarioData.TestScenarioStatus.TEST_SCENARIO_RUNNING;
        sc.startTime = System.currentTimeMillis();
        notifyScenario(sc);
        Utils.wait(500);
        _currentScenario = sc;
    }

    public void finishScenario(String scenarioName, String status) {
        TestScenarioData sc = new TestScenarioData();
        sc.testName = _testInExecution.replace("_", " ");
        sc.name = scenarioName;
        sc.finishTime = System.currentTimeMillis();
        if (status.equals("passed")) {
            sc.status = TestScenarioData.TestScenarioStatus.TEST_SCENARIO_PASSED;
        } else {
            sc.status = TestScenarioData.TestScenarioStatus.TEST_SCENARIO_FAILED;
        }
        notifyScenario(sc);
        _currentScenario = sc;
    }

    public void startStep(AtestLog log, TestStepData step) {
        AtestLogEvent logEvent = new AtestLogEvent(AtestLogEvent.LogType.INFO
                , log.me() + " STEP", step.getNameFull());
        log(logEvent);

        TestStepData s = new TestStepData(step);
        s.status = TestStepData.TestStepStatus.TEST_STEP_RUNNING;
        s.startTime = System.currentTimeMillis();
        _currentScenarioStep = s;
       // _engine.postScenarioStep(s);
    }

    public void passStep(TestStepData step) {
        TestStepData s = new TestStepData(step);
        s.status = TestStepData.TestStepStatus.TEST_STEP_PASSED;
        s.finishTime = System.currentTimeMillis();
        //_engine.postScenarioStep(s);
    }

    public void failStep(TestStepData step) {
        TestStepData s = new TestStepData(step);
        s.status = TestStepData.TestStepStatus.TEST_STEP_FAILED;
        s.finishTime = System.currentTimeMillis();
        //_engine.postScenarioStep(s);
    }

    public TestStepData getCurrentScenarioStep() {
        return _currentScenarioStep;
    }

    public boolean isCurrentScenarioInZombieMode() {
        if (_currentScenario == null)
            return false;

        return _currentScenario.status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_RUNNING;
    }
}
