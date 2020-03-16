package com.nimblefix;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Dashboard implements Initializable {
    @FXML Label icon1,icon2,connected_label,user_label;
    @FXML ImageView logo_box,logo3,logo4;

    public Stage curr_stg;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        icon1.setText("\uD83D\uDD8A");
        icon2.setText("\uD83D\uDC41");
        connected_label.setText("255.255.255.255");
        user_label.setText("Anirban");
        logo_box.setImage(new Image("file://"+ getClass().getResource("/resources/nimblefix_logo.png").getPath(), 200, 150, true, true));
        logo3.setImage(new Image("file://"+ getClass().getResource("/resources/server.png").getPath(), 20, 20, true, true));
        logo4.setImage(new Image("file://"+ getClass().getResource("/resources/user.png").getPath(), 20, 20, true, true));


    }
}
