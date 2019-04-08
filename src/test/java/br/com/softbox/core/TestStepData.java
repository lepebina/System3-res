package br.com.softbox.core;

import java.util.LinkedHashMap;

/**
 */
public class TestStepData {


    public enum TestStepStatus {
        TEST_STEP_READY
        , TEST_STEP_RUNNING
        , TEST_STEP_PASSED
        , TEST_STEP_FAILED
        , TEST_STEP_ZOMBIE

    }
    public String scenarioName;
    private String namePattern;
    public TestStepStatus status;
    public String name;
    public String nameFull;
    public LinkedHashMap<String, String> params = new LinkedHashMap<>();
    public LinkedHashMap<String, String> results = new LinkedHashMap<>();
    public String error;
    private final String PARAM_LIMIT = "%";
    public long startTime;
    public long finishTime;


    public TestStepData(String scenario, String stepName) {
        scenarioName = scenario;
        namePattern = stepName;
        status = TestStepStatus.TEST_STEP_READY;
        name = stepName;
        error = "";
        startTime = 0L;
        finishTime = 0L;
    }
    public TestStepData(TestStepData other) {
        scenarioName = other.scenarioName;
        namePattern = other.namePattern;
        status = other.status;
        name = other.name;
        params = new LinkedHashMap<>(other.params);
        results = new LinkedHashMap<>(other.results);
        error = other.error;
        startTime = other.startTime;
        finishTime = other.finishTime;
    }
    public TestStepData add(String key, String value) {
        params.put(key, value);
        return this;
    }
    public TestStepData result(String key, String value) {
        results.put(key, value);
        return this;
    }
    public String getNameFull() {
        nameFull = namePattern;
        params.forEach((k, v) -> {
            this.nameFull = this.nameFull.replace(PARAM_LIMIT + k + PARAM_LIMIT, v);
        });
        return nameFull.isEmpty() ? namePattern : nameFull;
    }

}
