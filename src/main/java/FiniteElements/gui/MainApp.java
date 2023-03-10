package FiniteElements.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.Objects;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("MainScene.css")).toExternalForm());
        scene.setOnKeyPressed(event -> {
            MainSceneController controller = loader.getController();
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                controller.btnClick();
            }
        });


        primaryStage.setTitle("Finite Elements Method");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
