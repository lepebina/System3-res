package br.com.softbox.appbf;

import br.com.softbox.core.TestData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The test case controller.
 */
public class AtestTestCaseModel {
    private final SimpleStringProperty testName;
    private final SimpleStringProperty testResult;
    private final SimpleStringProperty testElapsed;

    public AtestTestCaseModel(TestData td) {
        testName = new SimpleStringProperty(td.testName);
        testResult = new SimpleStringProperty("");
        testElapsed = new SimpleStringProperty("");
    }

    public StringProperty testNameProperty() {
        return testName;
    }

    public void setTestName(String name) {
        testName.set(name);
    }

    public StringProperty testResultProperty() {
        return testResult;
    }

    public void setTestResult(String result) {
        testResult.set(result);
    }

    public String getTitle() {
        return testName.getValue();
    }

    public StringProperty testElapsedProperty() {
        return testElapsed;
    }

    public void setTestElapsed(String elapsedTime) {
        testElapsed.set(elapsedTime);
    }
}
