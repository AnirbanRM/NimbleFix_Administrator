package com.nimblefix;

import com.nimblefix.core.Worker;
import com.sun.glass.ui.CommonDialogs;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
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
            if(currentWorker==null)return;
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

            if(currentWorker.getDP()!=null){
                ByteArrayInputStream bis = new ByteArrayInputStream(currentWorker.getDP());
                Image i = new Image(bis);
                dP.setImage(i);
            }
        });
    }

    public void save_clicked(MouseEvent mouseEvent) throws Exception {
        boolean newWorker = false;
        if(currentWorker == null) {
            currentWorker = new Worker();
            newWorker = true;
        }

        currentWorker.setName(name_box.getText());
        currentWorker.setDesignation(designation_box.getText());
        currentWorker.setEmail(email_box.getText());
        currentWorker.setEmpID(id_box.getText());
        currentWorker.setMobile(mobile_box.getText());
        try { currentWorker.setDoB(dob_box.getValue().toString()); }catch (NullPointerException e){ }
        try { currentWorker.setDoJ(doj_box.getValue().toString()); }catch (NullPointerException e){ }

        if(dP.getImage()!=null) {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(dP.getImage(), null), "png", s);
            currentWorker.setDP(s.toByteArray());
            s.close();
        }

        if(newWorker)
            WorkerManagement.workers.add(currentWorker);
        curr_stg.close();
    }

    public void cancel_clicked(MouseEvent mouseEvent) {
        curr_stg.close();
    }

    public void add_clicked(MouseEvent mouseEvent) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File (PNG,JPG,JPEG)", "*.png", ".jpg", ".jpeg"));

        File f = fileChooser.showOpenDialog(curr_stg);
        if (f == null) return;

        FileInputStream fis = new FileInputStream(f);
        Image i = new Image(fis, 150, 150, true, true);
        fis.close();

        dP.setImage(i);
    }
}
