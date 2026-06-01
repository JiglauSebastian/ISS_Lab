package com.slim.ui;

import com.slim.utils.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        AppContext.getInstance();
        navigateTo("login.fxml", "SLIM - Library Management");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void navigateTo(String fxml, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/slim/ui/" + fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(App.class.getResource("/com/slim/ui/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
