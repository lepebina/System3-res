package br.com.softbox.core;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class TestScenarioData {


    public enum TestScenarioStatus {
        TEST_SCENARIO_READY
        , TEST_SCENARIO_RUNNING
        , TEST_SCENARIO_PASSED
        , TEST_SCENARIO_FAILED
        , TEST_SCENARIO_ZOMBIE
    };

    public String testName;
    public String name;
    public TestScenarioStatus status;
    public List<TestStepData> steps = new ArrayList<>();
    public long startTime;
    public long finishTime;

    public TestScenarioData() {
        testName = "";
        name = "";
        status = TestScenarioStatus.TEST_SCENARIO_READY;
        startTime = 0L;
        finishTime = 0L;
    }
    public TestScenarioData(TestScenarioData other) {
        testName = other.testName;
        name = other.name;
        status = other.status;
        steps = other.steps;
        startTime = other.startTime;
        finishTime = other.finishTime;
    }
}
