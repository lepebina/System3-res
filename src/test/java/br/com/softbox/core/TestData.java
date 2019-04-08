package br.com.softbox.core;

import java.util.ArrayList;
import java.util.List;


public class TestData {
    public enum TestStatus { TEST_READY_TO_RUN
                           , TEST_ERROR_CUCUMBER_LOADING
                           , TEST_ERROR_CLASS_LOADING
                           , TEST_RUNNING
                           , TEST_PASSED
                           , TEST_FAILED};

    public boolean       enabled;                       // Should this test be executed in smoke tests bundle?
    public TestStatus    status;                        // The test status.
    public String        startTime;                     // When the test started its execution.
    public String        endTime;                       // When the test finish its execution.
    public String        testName;                      // The test title to be shown in GUI.
    public String        featureFilename;               // The feature file name.
    public String        featureContents;               // The feature file contents.
    public String        currentStepInExecution;        // TODO: implement me
    public List<String>  steps;                         // TODO: implement me
    public String        stepdefsFilename;              // XPTOTestSteps.java.
    public String        bddPkg;                        // The package name which contains this test.
    public String        bddTest;                       // XPTOTest.java
    public long          junitRuntime;                  // Test duration.
    public String        errorAfterJUnit;
    public String        report;
    public String        errorOnCurrentURL;

    public TestData() {
        enabled = false;
        status = TestStatus.TEST_READY_TO_RUN;
        startTime = "";
        endTime = "";
        testName = "";
        featureFilename = "";
        featureContents = "";
        currentStepInExecution = "";
        steps = new ArrayList<>();
        stepdefsFilename = "";
        bddPkg = "";
        bddTest = "";
        junitRuntime = 0L;
        errorAfterJUnit = "";
        report = "";
        errorOnCurrentURL = "";
    }

    /**
     * Copy constructor
     * @param other the other
     */
    public TestData(TestData other) {
        enabled = other.enabled;
        status = other.status;
        startTime = other.startTime;
        endTime = other.endTime;
        testName = other.testName;
        featureFilename = other.featureFilename;
        featureContents = other.featureContents;
        currentStepInExecution = other.currentStepInExecution;
        steps = other.steps;
        stepdefsFilename = other.stepdefsFilename;
        bddPkg = other.bddPkg;
        bddTest = other.bddTest;
        junitRuntime = other.junitRuntime;
        errorAfterJUnit = other.errorAfterJUnit;
        report = other.report;
        errorOnCurrentURL = other.errorOnCurrentURL;
    }
}
