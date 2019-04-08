package br.com.softbox.appbf;

import br.com.softbox.core.TestScenarioData;
import br.com.softbox.core.TestStepData;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 */
public class ScenarioExecution {
    private List<TestScenarioData> _scenarios = new ArrayList<>();

    public ScenarioExecution() {
    }
    /**
     * Updates the last inserted scenario
     * @param scenario
     */
    public void update(TestScenarioData scenario) {
        if (scenario.status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_RUNNING) {
            if (!_scenarios.isEmpty()) {
                TestScenarioData lastScenario = _scenarios.get(_scenarios.size() - 1);
                if (lastScenario.status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_RUNNING) {
                    lastScenario.status = TestScenarioData.TestScenarioStatus.TEST_SCENARIO_ZOMBIE;
                    lastScenario.finishTime = System.currentTimeMillis();
                }
            }
            _scenarios.add(scenario);
        } else {
            TestScenarioData last = _scenarios.get(_scenarios.size()-1);
            last.status = scenario.status;
            last.finishTime = scenario.finishTime;
        }
    }

    public void updateStep(TestStepData step) {
        if (_scenarios.isEmpty()) {
            return;
        }
        TestScenarioData lastScenario = _scenarios.get(_scenarios.size()-1);
        if (step.status == TestStepData.TestStepStatus.TEST_STEP_RUNNING) {
            if (!lastScenario.steps.isEmpty()) {
                TestStepData last = lastScenario.steps.get(lastScenario.steps.size() - 1);
                if (last.status == TestStepData.TestStepStatus.TEST_STEP_RUNNING) {
                    // Something went wrong.
                    last.status = TestStepData.TestStepStatus.TEST_STEP_ZOMBIE;
                    last.finishTime = System.currentTimeMillis();
                }
            }
            lastScenario.steps.add(new TestStepData(step));
        } else if (!lastScenario.steps.isEmpty()) {
            TestStepData s = lastScenario.steps.get(lastScenario.steps.size() - 1);
            s.status = step.status;
            s.error = step.error;
            s.finishTime = step.finishTime;
            s.params = step.params;
            s.results = step.results;
        }
    }

    public List<TestScenarioData> getScenarios() {
        return _scenarios;
    }

    public void clear() {
        _scenarios.clear();
    }
}
