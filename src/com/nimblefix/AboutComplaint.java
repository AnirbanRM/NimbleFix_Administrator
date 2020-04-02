package com.nimblefix;

import com.nimblefix.core.Category;
import com.nimblefix.core.Complaint;
import com.nimblefix.core.InventoryItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AboutComplaint implements Initializable {

    @FXML Label cID,cTS,iTitle,iCat,iID;
    @FXML TextArea cUR;

    Complaint complaint;
    InventoryItem inventory;
    ArrayList<Category> categories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setParam(Complaint complaint, InventoryItem inventoryItem, ArrayList<Category> categories){
        this.complaint = complaint;
        this.inventory = inventoryItem;
        this.categories = categories;
    }


    public void init() {
        new Thread(() -> Platform.runLater(()->{
            cID.setText(complaint.getComplaintID());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            sdf.setTimeZone(TimeZone.getDefault());
            String cts = sdf.format(Complaint.getDTDate(complaint.getComplaintDateTime()));

            cTS.setText(cts);
            iTitle.setText(inventory.getTitle());
            iID.setText(inventory.getId());
            cUR.setText(complaint.getUserRemarks());

            for(Category c : categories)
                if(c.getUniqueID().equals(inventory.getCategoryTag())) {
                    iCat.setText(c.getCategoryString());
                    break;
                }
        })).start();
    }
}
