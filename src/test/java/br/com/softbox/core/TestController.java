package br.com.softbox.core;


import java.util.HashMap;
import java.util.List;


public interface TestController {


    public enum EvidenceLevel {FULL, ON_ERROR, AT_THE_END}
    public enum RunningType {NONE, SMOKE, SINGLE};
    public enum WebBrowser {NONE, FIREFOX, CHROME, IE};

    public final String FIREFOX = "firefox";
    public final String CHROME = "chrome";
    public final String IE = "ie";

    public String getRootDir();
    public String getRawWorkspaceDir();
    public String getRawFeatureDir();
    public String getRawEvidenceDir();
    public void run(String testName, WebBrowser browser);
    public void stop(String testName);
    public void runAll(WebBrowser browser);
    public void stopAll();
    public boolean toggle(String testName) throws AtestExecutionException;
    HashMap<String, TestData> getTests();
    public void changeInitialSite(String site);
    public List<String> getAvailableHosts();
    public List<WebBrowser> getAvailableWebBrowsers();
    public void saveFeatureChangings(String testName, String newFeatureContents);
    public boolean isRunning();
    public boolean isWebDriverShutdown();
    public String getTraceFileContents(String testName);
    public String getTestNameInExecution();
    public void setEvidenceLevel(EvidenceLevel level);
    public void postScenario(TestScenarioData sc);
    public void postScenarioStep(TestStepData step);
    public List<String> getStores();
    public void setStore(String store, boolean backup);

    boolean isBrowserIntanceOfInternetExplorer() throws AtestExecutionException;

    public enum MainMenuOptionCode {LOGIN, LOGOUT, ACCOUNT, ORDERS, ADDRESSES, WISH_LIST};

    HashMap<MainMenuOptionCode, String> getMainMenuUrlOptions();
}
