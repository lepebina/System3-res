
package br.com.softbox.appbf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * The entry point for the ATEST/BF application.
 */
public class MainApp extends Application {
    private Stage _primaryStage;
    private BorderPane _rootLayout;
    AtestTestCaseController _controller = null;

    public MainApp() {
    }
    /**
     * Entry point.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        _primaryStage = primaryStage;
        _primaryStage.setTitle("ATEST/BF v4.1");

        initRootLayout();
        showMainView();
    }

    /**
     * Initializes the root layout (base layout).
     */
    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/appbf/root_layout_view.fxml"));
            _rootLayout = (BorderPane) loader.load();

            // Show the scene
            Scene scene = new Scene(_rootLayout);
            _primaryStage.setScene(scene);
            _primaryStage.setMaximized(true);
            _primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the Main application layout inside the root layout.
     */
    private void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/appbf/main_view.fxml"));
            AnchorPane mainAppView = (AnchorPane) loader.load();

            // Sets the main app view inside the root layout
            _rootLayout.setCenter(mainAppView);

            _controller = loader.getController();
            _controller.init(_primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
