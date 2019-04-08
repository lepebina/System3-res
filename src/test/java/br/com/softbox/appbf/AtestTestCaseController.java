package br.com.softbox.appbf;

import br.com.softbox.core.*;
import br.com.softbox.utils.Utils;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import org.fxmisc.richtext.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controlls the main window.
 */
public class AtestTestCaseController {
    private LinkedHashMap<String, ScenarioExecution> _scenarios = new LinkedHashMap<>();
    private LinkedHashMap<String, TestData> _tests;
    private String _currentFeatureInPresentation = "";
    private AtestTestCaseModel _testModelForTicking = null;
    private int _tickingSeconds = 0;
    private Timer _tickingTimer = null;
    private final String CLEAR_EXECUTION_TABLE_VIEWS = "CLEAR_EXECUTION_TABLE_VIEWS";
    private final String[] _gherkinKeywords = new String[]{
            "Given", "And", "When", "Scenario Outline", "Scenario", "But", "Then", "Feature", "Examples", "Background"
    };
    private final String _gherkinPatString = "\"([^\"]*)\"";
    private final String _gherkinPatKeyword = "\\b(" + String.join("|", _gherkinKeywords) + ")\\b";
    private final String _gherkinPatComment = "#[^\n]*";
    private final Pattern _gherkinPattern = Pattern.compile(
            "(?<KEYWORD>" + _gherkinPatKeyword + ")"
                    + "|(?<STRING>" + _gherkinPatString + ")"
                    + "|(?<COMMENT>" + _gherkinPatComment + ")"
    );
    private final String _logPatStartingTest = "Starting test .* \\.\\.\\.";
    private final String _logPatStep = ".* -.* STEP- .*";
    private final String _logPatScenario = ".* -.* SCENARIO- .*";
    private final String _logPatOops = ".* OOPS -.*- .*";
    private final String _logPatTrace = ".* TRACE -.*- .*";
    private final String _logPatStatusOK = ".* -.*- PASSED";
    private final String _logPatStatusFail = ".* -.*- FAILED";
    private final Pattern _logGherkinPattern = Pattern.compile(
            "(?<STARTING>" + _logPatStartingTest + ")"
                    + "|(?<STEP>" + _logPatStep + ")"
                    + "|(?<SCENARIO>" + _logPatScenario + ")"
                    + "|(?<OOPS>" + _logPatOops + ")"
                    + "|(?<TRACE>" + _logPatTrace + ")"
                    + "|(?<PASSED>" + _logPatStatusOK + ")"
                    + "|(?<FAILED>" + _logPatStatusFail + ")"
    );

    private ObservableList<AtestTestCaseModel> _testsModel = FXCollections.observableArrayList();
    private ObservableList<ScenarioModel> _scenariosModel = FXCollections.observableArrayList();
    private ObservableList<ScenarioStepModel> _scenarioStepsModel = FXCollections.observableArrayList();
    private CodeArea _codeFeature = new CodeArea();
    private CodeArea _logTextArea = new CodeArea();
    private AtestEngine _engine = new AtestEngine();
    @FXML
    private TableView<AtestTestCaseModel> tableTest;
    @FXML
    private TableColumn<AtestTestCaseModel, String> tableColTest;
    @FXML
    private TableColumn<AtestTestCaseModel, String> tableColResult;
    @FXML
    private TableColumn<AtestTestCaseModel, String> tableColElapsedTime;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;
    @FXML
    private ComboBox cboxSite;
    @FXML
    private Button btnSettings;
    @FXML
    private ComboBox cboxEvidenceLevel;
    @FXML
    private Button btnAddSite;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnAbout;
    @FXML
    private Button btnQuit;
    @FXML
    private AnchorPane anchorPaneFeature;
    @FXML
    private AnchorPane anchorPaneLog;
    @FXML
    private AnchorPane anchorExecution;
    @FXML
    private ImageView imgViewRunning;
    @FXML
    private TableView<ScenarioModel> tblViewScenarios;
    @FXML
    private TableColumn<ScenarioModel, ImageView> tblColScenarioStatus;
    @FXML
    private TableColumn<ScenarioModel, String> tblColScenarioName;
    @FXML
    private TableColumn<ScenarioModel, String> tblColScenarioDuration;
    @FXML
    private TableColumn<ScenarioModel, String> tblColScenarioIndex;
    @FXML
    private TableView<ScenarioStepModel> tblViewSteps;
    @FXML
    private TableColumn<ScenarioStepModel, ImageView> tblColStepStatus;
    @FXML
    private TableColumn<ScenarioStepModel, String> tblColStepName;
    @FXML
    private TableColumn<ScenarioStepModel, String> tblColStepDuration;
    @FXML
    private Tab tabExecution;
    @FXML
    private ComboBox cboxStore;
    @FXML
    private ImageView imgStore;

    private String _currentTestTitleSelected;
    private Stage _primaryStage;
    private final String _evidenceFull = "Full";
    private final String _evidenceOnErrors = "Errors";
    private final String _evidenceAtTheEnd = "At The End";
    private ScenarioExecution _scenarioInExecution = null;
    private int _scenarioTableIndex = 0;
    private boolean _changedStore = false;
    private String _currentStore;

    /**
     * Called by the MainApp to initialize some internals.
     *
     * @param primaryStage
     */
    public void init(Stage primaryStage) {
        Image img = new Image(String.valueOf(this.getClass().getResource("/img/gears.gif")));
        imgViewRunning.setImage(img);
        imgViewRunning.setVisible(false);
        _primaryStage = primaryStage;
        setToolbarButtonIcons();
        setEvidenceLevel();
        setCodeArea();
        setLogArea();
//        setExecutionScenarios();

        final String traceFilenameOnError = _engine.init();
        if (traceFilenameOnError.isEmpty()) {
            _engine.setListener(this);
            _tests = _engine.getTests();
            updateStore();
            updateSites(_engine.getSettingsSites());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ATEST Error Initialization");
            alert.setHeaderText("Something goes wrong during ATEST initialization");
            alert.setContentText("For further information,\nconsult [" + traceFilenameOnError + "]");
            alert.showAndWait();
        }
    }

    private void updateStore() {
        List<String> stores = _engine.getStores();
        ObservableList<String> options = FXCollections.observableArrayList(stores);
        cboxStore.setItems(options);
        cboxStore.getSelectionModel().select(0);
        _currentStore = cboxStore.getSelectionModel().getSelectedItem().toString();
        updateStoreIcon();
    }

    /**
     * List all test cases inside the table list.
     */
    private void fillTestsTable() {
        Platform.runLater(() -> {
            _tests.forEach((k, v) -> {
                _testsModel.add(new AtestTestCaseModel(v));
            });
            tableTest.setItems(_testsModel);
        });
    }

    private void cleanUpAfterChangingStore() {
        _tests.clear();
        cleanUp();
        _codeFeature.clear();
        _currentFeatureInPresentation = "";
        Platform.runLater(() -> {
            _testsModel.clear();
            tableTest.setItems(_testsModel);
        });
    }

    /**
     * Updates the combo box to the available sites
     */
    private void updateSites(List<String> sites) {
        ObservableList<String> options = FXCollections.observableArrayList(sites);
        cboxSite.setItems(options);
    }


    /**
     * Called by JavaFX
     */
    @FXML
    public void initialize() {
        btnSave.setDisable(true);
        btnStop.setDisable(true);
        initializeTestsTable();
        initializeScenariosTable();
        initializeScenarioStepsTable();
        fillTestsTable();
    }


    private void setToolbarButtonIcons() {
        btnStart.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/start.png"))));
        final Tooltip ttStart = new Tooltip();
        ttStart.setText("Starts the smoke test");
        btnStart.setTooltip(ttStart);

        btnStop.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/stop.png"))));
        final Tooltip ttStop = new Tooltip();
        ttStop.setText("Stops the smoke test");
        btnStop.setTooltip(ttStop);

        btnAddSite.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/add.png"))));
        final Tooltip ttAdd = new Tooltip();
        ttAdd.setText("Adds new host to the list");
        btnAddSite.setTooltip(ttAdd);

        btnSave.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/save.png"))));
        final Tooltip ttSave = new Tooltip();
        ttSave.setText("Saves the current feature file");
        btnSave.setTooltip(ttSave);

        btnSettings.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/settings.png"))));
        final Tooltip ttSettings = new Tooltip();
        ttSettings.setText("Settings");
        btnSettings.setTooltip(ttSettings);

        btnAbout.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/about.png"))));
        final Tooltip ttAbout = new Tooltip();
        ttAbout.setText("About");
        btnAbout.setTooltip(ttAbout);

        btnQuit.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/shutdown.png"))));
        final Tooltip ttQuit = new Tooltip();
        ttQuit.setText("Quit");
        btnQuit.setTooltip(ttQuit);
    }


    private void setLogArea() {
        anchorPaneLog.getChildren().add(_logTextArea);
        _logTextArea.prefHeightProperty().bind(anchorPaneLog.heightProperty());
        _logTextArea.prefWidthProperty().bind(anchorPaneLog.widthProperty());
        _logTextArea.setParagraphGraphicFactory(LineNumberFactory.get(_logTextArea));
        _logTextArea.richChanges().subscribe(change -> {
            _logTextArea.setStyleSpans(0, computeLogGherkinHighlighting(_logTextArea.getText()));
        });
        _primaryStage.getScene().getStylesheets().add(this.getClass().getResource("/appbf/log.css").toExternalForm());
    }

    private StyleSpans<Collection<String>> computeLogGherkinHighlighting(String text) {
        Matcher matcher = _logGherkinPattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("STARTING") != null ? "starting" :
                    matcher.group("STEP") != null ? "step" :
                    matcher.group("SCENARIO") != null ? "scenario" :
                    matcher.group("OOPS") != null ? "oops" :
                    matcher.group("PASSED") != null ? "passed" :
                    matcher.group("FAILED") != null ? "oops" :
                    matcher.group("TRACE") != null ? "trace" : null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private void setCodeArea() {
        anchorPaneFeature.getChildren().add(_codeFeature);
        _codeFeature.prefWidthProperty().bind(anchorPaneFeature.widthProperty());
        _codeFeature.prefHeightProperty().bind(anchorPaneFeature.heightProperty());
        _codeFeature.setParagraphGraphicFactory(LineNumberFactory.get(_codeFeature));
        _codeFeature.richChanges().subscribe(change -> {
            _codeFeature.setStyleSpans(0, computeGherkinHighlighting(_codeFeature.getText()));
        });
        _primaryStage.getScene().getStylesheets().add(this.getClass().getResource("/appbf/atest_gherkin.css").toExternalForm());
        _codeFeature.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue.isEmpty() || oldValue.equals(newValue)) {
                    btnSave.setDisable(true);
                } else {
                    btnSave.setDisable(false);
                }
            }
        });
        final ContextMenu ctxMenu = new ContextMenu();
        MenuItem commentItem = new MenuItem("Comment the selected area");
        commentItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                event.consume();// no consumer will get this event, just this one.
                String selectedText = _codeFeature.getSelectedText();
                if (selectedText.isEmpty()) {
                }
                String x = selectedText.replaceAll("^", "#");
                x = x.replaceAll("\n", "\n#");
                _codeFeature.replaceSelection(x);
            }
        });
        MenuItem uncommentItem = new MenuItem("Uncomment the selected area");
        uncommentItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                event.consume();// no consumer will get this event, just this one.
                String x = _codeFeature.getSelectedText().replaceAll("^#+", "");
                x = x.replaceAll("\n#+", "\n");
                _codeFeature.replaceSelection(x);
            }
        });
        ctxMenu.getItems().addAll(commentItem, uncommentItem);
        _codeFeature.setContextMenu(ctxMenu);

        _codeFeature.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                if (_codeFeature.getSelectedText().isEmpty()) {
                    _codeFeature.selectLine();
                }
                _codeFeature.setContextMenu(ctxMenu);
            }
        });
    }

    private StyleSpans<Collection<String>> computeGherkinHighlighting(String text) {
        Matcher matcher = _gherkinPattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("COMMENT") != null ? "comment" :
                                    matcher.group("STRING") != null ? "string" : null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @FXML
    public void handleAbout(ActionEvent ae) {
        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        dlg.setTitle("About");
        dlg.setHeaderText("Atest/BF - Automation Tests for Netshoes (Edition Black Friday 2015)");
        GridPane grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setAlignment(Pos.CENTER);
        ImageView img = new ImageView(String.valueOf(this.getClass().getResource("/img/softbox.png")));
        //Label version = new Label("VERSION: " + settings.getVersion());
        //grid.add(version, 0, 0);
        grid.add(img, 0, 0);
        dlg.getDialogPane().setContent(grid);
        dlg.showAndWait();
    }

    @FXML
    public void handleQuit(ActionEvent ae) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit");
        alert.setHeaderText("Do you really want to quit?");
        if (_engine.isRunning()) {
            alert.setContentText("There is test running at this time!!!");
        }
        ButtonType btnQuit = new ButtonType("Quit", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnQuit, btnCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == btnQuit) {
            _engine.shutdown();
            Utils.wait(1000);
            System.exit(0);
        }
    }

    public boolean showAlertForBackupStore() {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("BACKUP CURRENT FEATURES");
        dlg.setHeaderText("Your [" + _currentStore + "] features will be backed up!!!");
        dlg.setContentText("ATTENTION: This will **OVERWRITE** all features in [" + _currentStore + "]\nby the ones inside [feature] directory.");
        if (_currentStore.equals("NETSHOES")) {
            dlg.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/ns_ico.png"))));
        } else {
            dlg.setGraphic(new ImageView(String.valueOf(this.getClass().getResource("/img/zattini_ico.png"))));
        }

        ButtonType btnBackup = new ButtonType("BACKUP", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Do NOT backup", ButtonBar.ButtonData.CANCEL_CLOSE);
        dlg.getDialogPane().getButtonTypes().addAll(btnBackup, btnCancel);

        Optional<ButtonType> result = dlg.showAndWait();
        return result.get() == btnBackup;
    }

    /**
     * Configure table view.
     */
    private void initializeTestsTable() {
        //tableTest.getStylesheets().add(getClass().getResource("/appbf/testcases_table.css").toExternalForm());

        tableColTest.setCellValueFactory(cellData -> cellData.getValue().testNameProperty());
        tableColResult.setCellValueFactory(cellData -> cellData.getValue().testResultProperty());
        tableColResult.setStyle("-fx-alignment: CENTER");
        tableColElapsedTime.setCellValueFactory(cellData -> cellData.getValue().testElapsedProperty());
        tableColElapsedTime.setStyle("-fx-alignment: CENTER");

        // Detects the changes in table selection
        tableTest.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showFeature(newValue));

        // Setting context menu for rows
        tableTest.setRowFactory(
                new Callback<TableView<AtestTestCaseModel>, TableRow<AtestTestCaseModel>>() {
                    @Override
                    public TableRow<AtestTestCaseModel> call(TableView<AtestTestCaseModel> tblView) {
                        final TableRow<AtestTestCaseModel> row = new TableRow<AtestTestCaseModel>();
                        final ContextMenu rowMenu = new ContextMenu();

                        row.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                                showTestReport(row.getItem().getTitle());
                            }
                            updateScenarioExecution(row.getItem().getTitle());
                        });

                        // Menu option - RUN
                        MenuItem runItem = new MenuItem("Run");
                        runItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                event.consume();// no consumer will get this event, just this one.
                                final String testTitle = row.getItem().getTitle();
                                clearScenarioExecution(testTitle);
                                runSingleTest(testTitle);
                            }
                        });
                        // Menu option - STOP
                        MenuItem stopItem = new MenuItem("Stop");
                        stopItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                event.consume();// no consumer will get this event, just this one.
                                stopSingleTest(row.getItem().getTitle());
                            }
                        });
                        // Menu option - TRACE
                        MenuItem traceItem = new MenuItem("Open Trace File");
                        traceItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                showTraceFile(row.getItem().getTitle());
                            }
                        });
                        // Menu option - Enable/Disable
                        MenuItem enableDisableItem = new MenuItem("Enable/Disable");
                        enableDisableItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (toggleEnableDisableTest(row.getItem().getTitle())) {
                                    // Enabled
//                                info(row.getStyleClass().toString());
                                    row.getStyleClass().removeAll("norun");
                                } else {
                                    // Disabled
//                                info(row.getStyleClass().toString());
                                    row.getStyleClass().add("norun");
                                }
                            }
                        });
                        rowMenu.getItems().addAll(runItem, stopItem, traceItem, enableDisableItem);

                        // Only display context menu for non-null items:
                        row.contextMenuProperty().bind(
                                Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                        .then(rowMenu)
                                        .otherwise((ContextMenu) null)
                        );
                        return row;
                    }
                }
        );
    }

    private void showTestReport(String testTitle) {
        _tests = _engine.getTests();
        if (_tests.containsKey(testTitle)) {
            TestData td = _tests.get(testTitle);
            if (!td.report.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Report for [" + testTitle + "]");
                final WebView browser = new WebView();
                browser.getEngine().loadContent(td.report);
                browser.setPrefSize(1000, 500);
                ScrollPane scroll = new ScrollPane();
                scroll.setContent(browser);
                alert.getDialogPane().setContent(scroll);
                alert.showAndWait();
            }
        }
    }


    private void runSingleTest(String testTitle) {
        if (!_engine.isRunning()) {
            TestController.WebBrowser browser = chooseBrowser();
            if (browser == TestController.WebBrowser.NONE) {
                return;
            }
            _engine.run(testTitle, browser);
        } else {
            if (_engine.isWebDriverShutdown()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unable to Start");
                alert.setHeaderText("Shutdown in progress.");
                alert.setContentText("Wait until it finish off to start another test.\nTHIS CAN TAKE SEVERAL SECONDS...");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unable to Start");
                alert.setHeaderText("The test [" + _engine.getTestNameInExecution() + "] is running.");
                alert.setContentText("Wait until it finish off to start another test.");
                alert.showAndWait();
            }
        }
    }

    private void stopSingleTest(String testTitle) {
        _engine.stop(testTitle);
    }

    /**
     * Put the feature contents inside correct text area.
     *
     * @param newValue The feature contents.
     */
    private void showFeature(AtestTestCaseModel newValue) {
        if (!_currentFeatureInPresentation.isEmpty()
            && !_currentFeatureInPresentation.equals(newValue.getTitle())
            && !_tests.get(_currentFeatureInPresentation).featureContents.equals(_codeFeature.getText())) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Avoid Losing Your Changes");
            alert.setHeaderText("There are unsaved changes in the current feature [" + _currentFeatureInPresentation + "].");
            alert.setContentText("What do you want to do ?");
            ButtonType btnDiscard = new ButtonType("Discard Changes");
            ButtonType btnSaveAndGo = new ButtonType("Save And Go");
            ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnDiscard, btnSaveAndGo, btnCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == btnSaveAndGo) {
                _engine.saveFeatureChangings(_currentFeatureInPresentation, _codeFeature.getText());
            } else if (result.get() == btnCancel) {
                return;
            }
        }
        if (newValue != null) {
            btnSave.setDisable(true);
            String contents = getFeatureFileContents(newValue.getTitle());
            if (!contents.isEmpty()) {
                _currentFeatureInPresentation = newValue.getTitle();
                _codeFeature.clear();
                _codeFeature.appendText(contents);
                _codeFeature.positionCaret(0);
            }
        }
    }

    private String getFeatureFileContents(String testTitle) {
        if (_tests.containsKey(testTitle)) {
            return _tests.get(testTitle).featureContents;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ATest Error");
            alert.setHeaderText("Attempt to show feature file");
            alert.setContentText("Unknown feature [" + testTitle + "]");
            alert.showAndWait();
        }
        return "";
    }

    /**
     * Shows a dialog with the trace file contents.
     *
     * @param testTitle
     */
    private void showTraceFile(String testTitle) {
        String contents = _engine.getTraceFileContents(testTitle);
        if (!contents.isEmpty()) {
            Task task = new Task<Void>() {
                @Override
                public Void call() {
                    Platform.runLater(() -> {
                        ButtonType btn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        Dialog<String> dlg = new Dialog<String>();
                        dlg.setTitle("Trace");
                        dlg.setHeaderText("Test: " + testTitle);

                        TextArea txt = new TextArea(contents);
                        txt.setEditable(false);
                        txt.setWrapText(false);
                        txt.setMaxWidth(Double.MAX_VALUE);
                        txt.setMaxHeight(Double.MAX_VALUE);
                        GridPane.setVgrow(txt, Priority.ALWAYS);
                        GridPane.setHgrow(txt, Priority.ALWAYS);
                        GridPane gridContent = new GridPane();
                        gridContent.setMaxWidth(Double.MAX_VALUE);
                        gridContent.add(txt, 0, 0);
                        dlg.getDialogPane().setExpandableContent(gridContent);
                        dlg.getDialogPane().getButtonTypes().addAll(btn);
                        dlg.initModality(Modality.NONE);
                        dlg.showAndWait();
                    });
                    return null;
                }
            };
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.setName("[trace for " + testTitle + "] thread");
            th.start();
        }
    }

    private boolean toggleEnableDisableTest(String testTitle) {
        try {
            return _engine.toggle(testTitle);
        } catch (AtestExecutionException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ATest Error");
            alert.setHeaderText("Attempt to disable a test");
            alert.setContentText("Unknown test name [" + testTitle + "]: " + e.getMessage());
            alert.showAndWait();
        }
        return false;
    }

    @FXML
    public void handleStart(ActionEvent ae) {
        if (!_engine.isRunning()) {
            TestController.WebBrowser browser = chooseBrowser();
            if (browser == TestController.WebBrowser.NONE) {
                return;
            }
            cleanUp();
            cboxSite.setDisable(true);
            cboxStore.setDisable(true);
            _engine.runAll(browser);
        }
    }

    /**
     * Get the event raise by stop button pressing.
     *
     * @param ae the event
     */
    @FXML
    public void handleStop(ActionEvent ae) {
        _engine.stopAll();
    }

    @FXML
    public void handleSettings(ActionEvent ae) {
        Dialog<Pair<String, String>> dlg = new Dialog<>();
        dlg.setTitle("Settings");
        dlg.setHeaderText("Atest/Black Friday");

        ButtonType btnOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(btnOK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField rootDir = new TextField();
        rootDir.setPromptText("ex: \"C:\" \"/home/myusername\"");
        rootDir.setMaxWidth(Double.MAX_VALUE);
        rootDir.setEditable(false);

        TextField workspaceDir = new TextField();
        workspaceDir.setPromptText("Atest/BF install dir");
        workspaceDir.setMaxWidth(Double.MAX_VALUE);
        workspaceDir.setEditable(false);

        TextField featureDir = new TextField();
        featureDir.setPromptText("subdir under workspace");
        featureDir.setMaxWidth(Double.MAX_VALUE);
        featureDir.setEditable(false);

        TextField evidenceDir = new TextField();
        evidenceDir.setPromptText("subdir under workspace");
        evidenceDir.setMaxWidth(Double.MAX_VALUE);
        evidenceDir.setEditable(false);

        grid.add(new Label("Atest root directory:"), 0, 0);
        grid.add(rootDir, 1, 0);

        grid.add(new Label("Workspace directory:"), 0, 1);
        grid.add(workspaceDir, 1, 1);

        grid.add(new Label("Feature directory:"), 0, 2);
        grid.add(featureDir, 1, 2);

        grid.add(new Label("Evidence directory:"), 0, 3);
        grid.add(evidenceDir, 1, 3);

        grid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        dlg.getDialogPane().setContent(grid);

        rootDir.setText(_engine.getRootDir());
        workspaceDir.setText(_engine.getRawWorkspaceDir());
        featureDir.setText(_engine.getRawFeatureDir());
        evidenceDir.setText(_engine.getRawEvidenceDir());

        Optional<Pair<String, String>> result = dlg.showAndWait();
    }

    /**
     * Adds a new value to the combo box site
     *
     * @param ae
     */
    @FXML
    public void handleAddSite(ActionEvent ae) {
//        Platform.runLater(() -> {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Add New Site");
        dialog.setHeaderText("A 'site' is the URL the every scenario should load initially.");
        dialog.setContentText("URL (without www):");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            final String host = result.get();
            if (!_engine.getAvailableHosts().contains(host)) {
                cboxSite.getItems().add(host);
            }
        }
//        });
    }

    @FXML
    public void handleCboxSite(ActionEvent ae) {
        String selectedSite = cboxSite.getSelectionModel().getSelectedItem().toString();
        _engine.changeInitialSite(selectedSite);
        _tests = _engine.getTests();
        showCurrentFeatureAgain();
    }

    @FXML
    public void handleCboxStore(ActionEvent ae) {
        final String selectedStore = cboxStore.getSelectionModel().getSelectedItem().toString();
        if (!_currentStore.equals(selectedStore)) {
            boolean backup = showAlertForBackupStore();
            _currentStore = selectedStore;
            _changedStore = true;
            cleanUpAfterChangingStore();
            _engine.setStore(selectedStore, backup);
            _tests = _engine.getTests();
            fillTestsTable();
            updateSites(_engine.getSettingsSites());
            updateStoreIcon();
            _changedStore = false;
        }
    }

    private void updateStoreIcon() {
        if (_currentStore.equals("NETSHOES")) {
            imgStore.setImage(new Image(String.valueOf(this.getClass().getResource("/img/ns_ico.png"))));
        } else {
            imgStore.setImage(new Image(String.valueOf(this.getClass().getResource("/img/zattini_ico.png"))));
        }
    }

    public void setEvidenceLevel() {
        List<String> levels = new ArrayList<>();
        levels.add(_evidenceFull);
        levels.add(_evidenceAtTheEnd);
        levels.add(_evidenceOnErrors);
        ObservableList<String> options = FXCollections.observableArrayList(levels);
        cboxEvidenceLevel.setItems(options);
        _engine.setEvidenceLevel(TestController.EvidenceLevel.ON_ERROR);
        cboxEvidenceLevel.getSelectionModel().select(_evidenceOnErrors);
    }

    @FXML
    public void handleEvidenceLevel(ActionEvent ae) {
        String selected = cboxEvidenceLevel.getSelectionModel().getSelectedItem().toString();
        TestController.EvidenceLevel level = TestController.EvidenceLevel.ON_ERROR;
        if (selected.equals(_evidenceFull)) {
            level = TestController.EvidenceLevel.FULL;
        } else if (selected.equals(_evidenceAtTheEnd)) {
            level = TestController.EvidenceLevel.AT_THE_END;
        } else if (selected.equals(_evidenceOnErrors)) {
            level = TestController.EvidenceLevel.ON_ERROR;
        }
        _engine.setEvidenceLevel(level);
    }

    /**
     * Shows the feature contents that has been shown shown before changing the host (combo box)
     */
    private void showCurrentFeatureAgain() {
        Platform.runLater(() -> {
            _codeFeature.clear();
            if (!_currentFeatureInPresentation.isEmpty()) {
                _codeFeature.appendText(getFeatureFileContents(_currentFeatureInPresentation));
                _codeFeature.positionCaret(0);
            }
        });
    }

    /**
     * Get the event by save button pressing. This will save the features changes.
     */
    @FXML
    public void handleSaveFeature() {
        Platform.runLater(() -> {
            if (!_codeFeature.getText().isEmpty()) {
                btnSave.setDisable(true);
                _engine.saveFeatureChangings(_currentFeatureInPresentation, _codeFeature.getText());
                _codeFeature.deselect();
            }
        });
    }

    /**
     * Presents a dialog to the user choose the web browser.
     *
     * @return
     */
    private TestController.WebBrowser chooseBrowser() {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Choose a Browser!");

        ButtonType cancel = new ButtonType("CANCEL", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(cancel, ok);
        Node okNode = dlg.getDialogPane().lookupButton(ok);
        okNode.setDisable(true);

        final List<TestController.WebBrowser> availableBrowsers = _engine.getAvailableWebBrowsers();

        final ToggleGroup group = new ToggleGroup();
        ToggleButton firefox = new ToggleButton("", new ImageView(String.valueOf(this.getClass().getResource("/img/firefox.png"))));
        firefox.setToggleGroup(group);
        firefox.setOnMouseClicked(e -> {
            Node okbtn = dlg.getDialogPane().lookupButton(ok);
            if (firefox.isSelected()) {
                okbtn.setDisable(false);
            } else {
                okbtn.setDisable(true);
            }
        });
        if (!availableBrowsers.contains(TestController.WebBrowser.FIREFOX)) {
            firefox.setSelected(false);  // TODO: verificar se isso desabilita o botao
        }

        ToggleButton chrome = new ToggleButton("", new ImageView(String.valueOf(this.getClass().getResource("/img/chrome.png"))));
        chrome.setToggleGroup(group);
        chrome.setOnMouseClicked(e -> {
            Node okbtn = dlg.getDialogPane().lookupButton(ok);
            if (chrome.isSelected()) {
                okbtn.setDisable(false);
            } else {
                okbtn.setDisable(true);
            }
        });
        if (!availableBrowsers.contains(TestController.WebBrowser.CHROME)) {
            chrome.setSelected(false);  // TODO: verificar se isso desabilita o botao
        }

        ToggleButton ie = new ToggleButton("", new ImageView(String.valueOf(this.getClass().getResource("/img/ie.png"))));
        ie.setToggleGroup(group);
        ie.setOnMouseClicked(e -> {
            Node okbtn = dlg.getDialogPane().lookupButton(ok);
            if (ie.isSelected()) {
                okbtn.setDisable(false);
            } else {
                okbtn.setDisable(true);
            }
        });
        if (!availableBrowsers.contains(TestController.WebBrowser.IE)) {
            ie.setSelected(false);  // TODO: verificar se isso desabilita o botao
        }
        GridPane pane = new GridPane();
        pane.add(firefox, 0, 0);
        pane.add(chrome, 1, 0);
        pane.add(ie, 2, 0);
        dlg.getDialogPane().setContent(pane);

        TestController.WebBrowser browser = TestController.WebBrowser.NONE;
        Optional<ButtonType> result = dlg.showAndWait();

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            if (result.isPresent()) {
                if (firefox.isSelected()) {
                    browser = TestController.WebBrowser.FIREFOX;
                } else if (chrome.isSelected()) {
                    browser = TestController.WebBrowser.CHROME;
                } else if (ie.isSelected()) {
                    browser = TestController.WebBrowser.IE;
                }
            }
        }
        return browser;
    }

    /**
     * Configure the availabity for the smoke test control buttons (start and stop)
     *
     * @param start True to disable start button, otherwise false.
     * @param stop  True to disable stop button, otherwise false.
     */
    public void setDisableStartStop(boolean start, boolean stop) {
        btnStart.setDisable(start);
        btnStop.setDisable(stop);
    }

    /**
     * Cleans up the tests status and log window.
     */
    private void cleanUp() {
        _logTextArea.clear();
        for (AtestTestCaseModel test : _testsModel) {
            test.testElapsedProperty().setValue("");
            test.testResultProperty().setValue("");
        }
        clearScenarioExecution("ALL");
    }

    /**
     * Changes the test result for a given test name.
     *
     * @param testTitle the test name.
     */
    private void setTestStatus(String sResult, String testTitle) {
        for (AtestTestCaseModel test : _testsModel) {
            if (test.getTitle().equals(testTitle)) {
                test.testResultProperty().setValue(sResult);
                break;
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listenToLogEvent(AtestLogEvent event) {

        final String text = Utils.formatLogEvent(event);
        Platform.runLater(() -> {
            if (event.getLogType() == AtestLogEvent.LogType.TRACE) {
                return;
            }
            _logTextArea.appendText(text);
        });
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listenToTestDataEvent(TestData testData) {
        TestData td = new TestData(testData);
        processFreshTestData(td);
    }

    private String convertStatusToStr(TestData.TestStatus status) {
        String s = "";
        switch (status) {
            case TEST_ERROR_CLASS_LOADING:
            case TEST_ERROR_CUCUMBER_LOADING:
            case TEST_FAILED:
                s = "FAILED";
                break;
            case TEST_PASSED:
                s = "PASSED";
                break;
            case TEST_RUNNING:
                s = "RUNNING";
                break;
            default:
                break;
        }
        return s;
    }

    private void processFreshTestData(TestData td) {
        try {
            TestData internalTestData = _tests.get(td.testName);
            internalTestData = td;
            updateTimer(td);
            setTestStatus(convertStatusToStr(td.status), td.testName);

            if (td.status == TestData.TestStatus.TEST_ERROR_CUCUMBER_LOADING) {
                Platform.runLater(() -> {
                    AtestLogEvent event = new AtestLogEvent(AtestLogEvent.LogType.OOPS, "ATEST core", "class ["
                            + td.bddTest + "], JAR was generated with wrong paths.\n"
                            + td.errorAfterJUnit);
                    _logTextArea.appendText(Utils.formatLogEvent(event));
                });
            }
            if (td.status == TestData.TestStatus.TEST_PASSED) {
                Platform.runLater(() -> {
                    AtestLogEvent event = new AtestLogEvent(AtestLogEvent.LogType.INFO, td.testName, "PASSED");
                    _logTextArea.appendText(Utils.formatLogEvent(event));
                });
            }
            if (td.status == TestData.TestStatus.TEST_FAILED) {
                Platform.runLater(() -> {
                    AtestLogEvent event = new AtestLogEvent(AtestLogEvent.LogType.OOPS, td.testName, "FAILED");
                    _logTextArea.appendText(Utils.formatLogEvent(event));
                    AtestLogEvent eventURL = new AtestLogEvent(AtestLogEvent.LogType.INFO, td.testName, "failed at URL : " + td.errorOnCurrentURL);
                    _logTextArea.appendText(Utils.formatLogEvent(eventURL));
                });
            }
        } catch (Exception e) {
            ;
        }
    }

    private void updateTimer(TestData td) {
        switch (td.status) {
            case TEST_ERROR_CLASS_LOADING:
            case TEST_ERROR_CUCUMBER_LOADING:
            case TEST_FAILED:
            case TEST_PASSED:
                _tickingTimer.cancel();
                _tickingSeconds = 0;
                break;
            case TEST_RUNNING:
                startTicking(td);
                break;
            default:
                break;
        }
    }

    private void startTicking(TestData td) {
        _testModelForTicking = getTestModel(td.testName);
        _tickingTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                NumberFormat twoDigitFormat = new DecimalFormat("00");
                long min = _tickingSeconds / 60;
                long secRemainder = _tickingSeconds - (min * 60);
                String time = twoDigitFormat.format(min) + ":" + twoDigitFormat.format(secRemainder);
                setElapsedTime(time);
                _tickingSeconds++;
            }
        };
        _tickingTimer.scheduleAtFixedRate(task, 1000, 1000);
    }

    private AtestTestCaseModel getTestModel(String testName) {
        for (AtestTestCaseModel test : _testsModel) {
            if (test.getTitle().equals(testName)) {
                return test;
            }
        }
        return null;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listenToIsRunning(TestController.RunningType runningType) {
        if (runningType == TestController.RunningType.NONE) {
            imgViewRunning.setVisible(false);
            cboxSite.setDisable(false);
            cboxStore.setDisable(false);
            setDisableStartStop(false, true);
            if (_tickingTimer != null) {
                _tickingTimer.cancel();
            }
        } else if (runningType == TestController.RunningType.SINGLE) {
            imgViewRunning.setVisible(true);
            cboxSite.setDisable(false);
            cboxStore.setDisable(true);
            setDisableStartStop(true, true);
        } else if (runningType == TestController.RunningType.SMOKE) {
            imgViewRunning.setVisible(true);
            cboxSite.setDisable(true);
            cboxStore.setDisable(true);
            setDisableStartStop(true, false);
        }
    }

    private void setElapsedTime(String elapsedTime) {
        Platform.runLater(() -> {
            _testModelForTicking.testElapsedProperty().setValue(elapsedTime);
        });
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listenToScenario(TestScenarioData scenario) {
        TestScenarioData sc = new TestScenarioData(scenario);
        processFreshScenarioData(sc);
    }

    private void processFreshScenarioData(TestScenarioData scenario) {
        final String testNameInExecution = _engine.getTestNameInExecution();
        if (scenario.status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_RUNNING) {
            if (!_scenarios.containsKey(testNameInExecution)) {
                ScenarioExecution execution = new ScenarioExecution();
                _scenarios.put(testNameInExecution, execution);
            }
            _scenarioInExecution = _scenarios.get(testNameInExecution);
        }
        if (_scenarioInExecution != null) {
            _scenarioInExecution.update(scenario);
            if (testNameInExecution.equals(scenario.testName)) {
                updateScenarioExecution(testNameInExecution);
            }
        }
    }

    private void updateScenarioExecution(String testTitle) {
        String testName = _engine.getTestNameInExecution();
        if (testName.isEmpty()) {  // No test is running anymore, let's pick up the selected one on Table Tests
            testName = tableTest.getSelectionModel().getSelectedItem().getTitle();
        }
        if (!testName.equals(testTitle)) {
            clearScenarioExecution(CLEAR_EXECUTION_TABLE_VIEWS);
        }
        fillTableScenarios(testTitle);
    }

    private void clearScenarioExecution(String testTitle) {
        if (!testTitle.equals(CLEAR_EXECUTION_TABLE_VIEWS)) {
            if (testTitle.equals("ALL")) {
                _scenarios.clear();
            } else if (!_scenarios.isEmpty() && _scenarios.containsKey(testTitle)) {
                _scenarios.get(testTitle).clear();
            }
        }
        _scenariosModel.clear();
        tblViewScenarios.setItems(_scenariosModel);

        _scenarioStepsModel.clear();
        tblViewSteps.setItems(_scenarioStepsModel);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listenToScenarioStep(TestStepData step) {
        TestStepData s = new TestStepData(step);
        processFreshScenarioStepData(s);
    }

    private void processFreshScenarioStepData(TestStepData step) {
        if (_scenarioInExecution != null) {
            _scenarioInExecution.updateStep(step);
        }
    }

    private void fillTableScenarios(String testTitle) {
        final String selectedTest = tableTest.getSelectionModel().getSelectedItem().getTitle();
        if (!selectedTest.equals(testTitle)) {
            return;
        }
        tabExecution.textProperty().set(testTitle + " (Scenario Details)");
        _scenarioTableIndex = 0;

        clearScenarioExecution(CLEAR_EXECUTION_TABLE_VIEWS);
        _currentTestTitleSelected = testTitle;
        if (_scenarios.containsKey(testTitle)) {
            ScenarioExecution scenarioExec = _scenarios.get(testTitle);
            scenarioExec.getScenarios().forEach(data -> {
                String duration = "";
                if (data.status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_FAILED
                   || data.status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_PASSED) {
                   duration = Utils.getElapsedTimeFormatted(data.startTime, data.finishTime);
                }
                _scenariosModel.add(
                        new ScenarioModel(data.name, data.status, duration, String.valueOf(++_scenarioTableIndex)));
            });
        }
        Platform.runLater(() -> {
                tblViewScenarios.setItems(_scenariosModel);
        });
    }

    private void initializeScenariosTable() {
        tblColScenarioName.setCellValueFactory(cellData -> cellData.getValue().name());
        tblColScenarioDuration.setCellValueFactory(cellData -> cellData.getValue().duration());
        tblColScenarioDuration.setStyle("-fx-alignment: CENTER");
        tblColScenarioIndex.setCellValueFactory(cellData -> cellData.getValue().index());
        tblColScenarioIndex.setStyle("-fx-alignment: CENTER");
        tblColScenarioStatus.setCellValueFactory(c -> new SimpleObjectProperty<ImageView>(new ImageView(c.getValue().getIcon())));
        tblColScenarioStatus.setStyle("-fx-alignment: CENTER");

        tblViewScenarios.setRowFactory(new Callback<TableView<ScenarioModel>, TableRow<ScenarioModel>>() {
            @Override
            public TableRow<ScenarioModel> call(TableView<ScenarioModel> param) {
                final TableRow<ScenarioModel> row = new TableRow<ScenarioModel>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1 && (!row.isEmpty())) {
                        showScenarioSteps(row.getIndex());
                    }
                });
                return row;
            }
        });
    }

    private void initializeScenarioStepsTable() {
        tblColStepName.setCellValueFactory(cellData -> cellData.getValue().nameFullProperty());
        tblColStepDuration.setCellValueFactory(cellData -> cellData.getValue().duration());
        tblColStepDuration.setStyle("-fx-alignment: CENTER");
        tblColStepStatus.setCellValueFactory(c -> new SimpleObjectProperty<ImageView>(new ImageView(c.getValue().getIcon())));
        tblColStepStatus.setStyle("-fx-alignment: CENTER");

        tblViewSteps.setRowFactory(new Callback<TableView<ScenarioStepModel>, TableRow<ScenarioStepModel>>() {
            @Override
            public TableRow<ScenarioStepModel> call(TableView<ScenarioStepModel> param) {
                final TableRow<ScenarioStepModel> row = new TableRow<ScenarioStepModel>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        showScenarioStepDetail(row.getIndex());
                    }
                });
                return row;
            }
        });
    }

    private void showScenarioSteps(int index) {
        ScenarioExecution execution = _scenarios.get(_currentTestTitleSelected);
        TestScenarioData selectedScenario = execution.getScenarios().get(
                tblViewScenarios.getSelectionModel().getSelectedIndex());

        if (selectedScenario.status == TestScenarioData.TestScenarioStatus.TEST_SCENARIO_RUNNING) {
            showAlertToWaitScenarioFinish(selectedScenario.testName, selectedScenario.name);
            return;
        }
        Platform.runLater(() -> {
            _scenarioStepsModel.clear();
            if (_scenarios.containsKey(_currentTestTitleSelected)) {
                ScenarioExecution scenarioExec = _scenarios.get(_currentTestTitleSelected);
                List<TestScenarioData> scenarios = scenarioExec.getScenarios();
                if (!scenarios.isEmpty() && index <= scenarios.size() - 1) {
                    scenarios.get(index).steps.forEach(step -> {
                        _scenarioStepsModel.add(new ScenarioStepModel(step));
                    });
                }
            }
            tblViewSteps.setItems(_scenarioStepsModel);
        });
    }

    private void showScenarioStepDetail(int index) {
        if (!_scenarios.containsKey(_currentTestTitleSelected)) {
            return;
        }
        Platform.runLater(() -> {
            ButtonType btn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dlg = new Dialog<String>();
            dlg.setTitle("Step Details");
            ScenarioExecution scenarioExec = _scenarios.get(_currentTestTitleSelected);

            List<TestStepData> steps = scenarioExec.getScenarios().get(
                    tblViewScenarios.getSelectionModel().getSelectedIndex()).steps;
            if (!steps.isEmpty() && index > steps.size()-1) {
                return;
            }
            ObservableList<ScenarioStepValue> data = FXCollections.observableArrayList();
            TestStepData step = steps.get(index);

            Set setParams = step.params.entrySet();
            Iterator i = setParams.iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                data.add(new ScenarioStepValue(e.getKey().toString(), e.getValue().toString()));
            }
            Set setResults = step.results.entrySet();
            i = setResults.iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                data.add(new ScenarioStepValue(e.getKey().toString(), e.getValue().toString()));
            }
            if (!step.error.isEmpty()) {
                data.add(new ScenarioStepValue("ERROR", step.error));
            }
            dlg.setHeaderText("Test: " + _currentTestTitleSelected
                    + "\nScenario: " + tblViewScenarios.getSelectionModel().getSelectedItem().getName());
            TableView<ScenarioStepValue> table = new TableView<ScenarioStepValue>();
            table.setEditable(false);
            TableColumn keyCol = new TableColumn("Entry Name");
            keyCol.setCellValueFactory(new PropertyValueFactory<ScenarioStepValue, String>("key"));
            TableColumn valCol = new TableColumn("Value");
            valCol.setCellValueFactory(new PropertyValueFactory<ScenarioStepValue, String>("value"));
            valCol.setEditable(true);
            table.setItems(data);
            table.getColumns().addAll(keyCol, valCol);
            dlg.getDialogPane().setContent(table);
            dlg.getDialogPane().getButtonTypes().addAll(btn);
            dlg.getDialogPane().setPrefSize(350, 200);
            dlg.setResizable(true);
            dlg.initModality(Modality.NONE);
            dlg.showAndWait();
        });
    }

    private void showAlertToWaitScenarioFinish(String testName, String scenarioName) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(testName);
            alert.setHeaderText("Scenario: " + scenarioName + " is RUNNING...");
            alert.setContentText("Please, wait for scenario to finish in order to see its details.");
            alert.showAndWait();
        });
    }

    public static class ScenarioStepValue {
        private final SimpleStringProperty key;
        private final SimpleStringProperty value;

        public ScenarioStepValue(String k, String v) {
            key = new SimpleStringProperty(k);
            value = new SimpleStringProperty(v);
        }
        public StringProperty key() {
            return key;
        }
        public StringProperty value() {
            return value;
        }
        public String getKey() {
            return key.getValue();
        }
        public String getValue() {
            return value.getValue();
        }
    }

}