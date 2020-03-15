package com.nimblefix;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginUIController implements Initializable {
    @FXML ImageView logo;
    @FXML TextField IP,Admin;
    @FXML PasswordField Password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image img = new Image("file://"+ getClass().getResource("/resources/nimblefix_logo.png").getPath(), 500, 150, false, true);
        logo.setImage(img);
    }

    public void signin_clicked(MouseEvent mouseEvent) {
        Thread client_thd = new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client(IP.getText(),Admin.getText(),Password.getText());
            }
        });
        client_thd.start();
    }
}