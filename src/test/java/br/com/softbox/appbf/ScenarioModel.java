package br.com.softbox.appbf;

import br.com.softbox.core.TestScenarioData;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Created by alexandermiro on 30/11/2015.
 */
public class ScenarioModel {
    private final SimpleStringProperty _name;
    private TestScenarioData.TestScenarioStatus _status;
    private final SimpleStringProperty _duration;
    private SimpleStringProperty _index;
    private SimpleObjectProperty ico;

    public ScenarioModel(String name
                        , TestScenarioData.TestScenarioStatus status
                        , String duration
                        , String index) {
        _name = new SimpleStringProperty(name);
        _status = status;
        _duration = new SimpleStringProperty(duration);
        _index = new SimpleStringProperty(index);
    }
    public StringProperty name() {
        return _name;
    }
    public String getName() {
        return _name.getValue();
    }
    public StringProperty duration() {
        return _duration;
    }
    public StringProperty index() {
        return _index;
    }
    public ScenarioModel ico() {
        return this;
    }
    public Image getIcon() {
        if (_status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_PASSED) {
            return new Image(String.valueOf(this.getClass().getResource("/img/passed.png")));
        } else if (_status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_FAILED) {
            return new Image(String.valueOf(this.getClass().getResource("/img/fail.png")));
        } else if (_status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_RUNNING) {
            return new Image(String.valueOf(this.getClass().getResource("/img/wait.gif")));
        } else if (_status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_ZOMBIE) {
            return new Image(String.valueOf(this.getClass().getResource("/img/zombie.png")));
        }
        return new Image(String.valueOf(this.getClass().getResource("/img/zombie.png")));
    }
}
