package br.com.softbox.appbf;

import br.com.softbox.core.TestScenarioData;
import br.com.softbox.core.TestStepData;
import br.com.softbox.utils.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

/**
 * Created by alexandermiro on 30/11/2015.
 */
public class ScenarioStepModel {
    private final SimpleStringProperty _nameFull;
    private SimpleStringProperty _duration;
    private TestStepData _step = null;

    public ScenarioStepModel(TestStepData step) {
        _step = new TestStepData(step);
        _nameFull = new SimpleStringProperty("");
        _duration = new SimpleStringProperty("");

    }
    public StringProperty nameFullProperty() {
        _nameFull.setValue(_step.getNameFull());
        return _nameFull;
    }
    public String getNameFullProperty() {
        return _nameFull.getValue();
    }

    public StringProperty duration() {
        if (_step.status == TestStepData.TestStepStatus.TEST_STEP_PASSED
           || _step.status == TestStepData.TestStepStatus.TEST_STEP_FAILED) {
            _duration.setValue(Utils.getElapsedTimeFormatted(_step.startTime, _step.finishTime));
        } else {
            _duration.setValue("");
        }
        return _duration;
    }

    public Image getIcon() {
        if (_step.status == TestStepData.TestStepStatus.TEST_STEP_PASSED) {
            return new Image(String.valueOf(this.getClass().getResource("/img/thumb_passed.png")));
        } else if (_step.status == TestStepData.TestStepStatus.TEST_STEP_FAILED) {
            return new Image(String.valueOf(this.getClass().getResource("/img/thumb_fail.png")));
        }
        return new Image(String.valueOf(this.getClass().getResource("/img/zombie.png")));
    }
}
