package com.engine.canada;

import Dictionary.DictionaryManagement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    public static DictionaryManagement dm = new DictionaryManagement();
    @Override
    public void start(Stage primaryStage) throws Exception{
        DictionaryManagement.insertFromFile();
        Parent root = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
        primaryStage.setTitle("Dictionary Application");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

}