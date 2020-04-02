package com.nimblefix;

import com.nimblefix.core.Worker;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AboutWorker implements Initializable {

    public Stage curr_stg;
    Worker currentWorker;
    ArrayList<String> desigs;
    @FXML ImageView dP;
    @FXML TextField id_box,name_box,designation_box,email_box,mobile_box;
    @FXML DatePicker dob_box,doj_box;

    public void setWorker(Worker w, ArrayList<String> desigs) {
        this.currentWorker = w;
        this.desigs = desigs;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()-> {
            id_box.setText(currentWorker.getEmpID());
            name_box.setText(currentWorker.getName());
            designation_box.setText(currentWorker.getDesignation());
            email_box.setText(currentWorker.getEmail());
            mobile_box.setText(currentWorker.getMobile());
            try {
                dob_box.setValue(LocalDate.parse(currentWorker.getDoB()));
                doj_box.setValue(LocalDate.parse(currentWorker.getDoJ()));
            }catch (DateTimeParseException e){ }

            TextFields.bindAutoCompletion(designation_box,desigs);
        });
    }

    public void save_clicked(MouseEvent mouseEvent) {
        currentWorker.setName(name_box.getText());
        currentWorker.setDesignation(designation_box.getText());
        currentWorker.setEmail(email_box.getText());
        currentWorker.setEmpID(id_box.getText());
        currentWorker.setMobile(mobile_box.getText());
        try { currentWorker.setDoB(dob_box.getValue().toString()); }catch (NullPointerException e){ }
        try { currentWorker.setDoJ(doj_box.getValue().toString()); }catch (NullPointerException e){ }
        curr_stg.close();
    }

    public void cancel_clicked(MouseEvent mouseEvent) {
        curr_stg.close();
    }
}
