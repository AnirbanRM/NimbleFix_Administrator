package com.nimblefix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SpectatorUI.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Login");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1200, 700));

        ((Spectator)loader.getController()).curr_stg = primaryStage;
        primaryStage.show();



//        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginUI.fxml"));
//        Parent root = loader.load();
//        primaryStage.setTitle("Login");
//        primaryStage.setResizable(false);
//        primaryStage.setScene(new Scene(root, 800, 400));
//
//        ((LoginUIController)loader.getController()).curr_stg = primaryStage;
//        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
