package com.nimblefix;

import com.nimblefix.ControlMessages.WorkerExchangeMessage;
import com.nimblefix.core.Category;
import com.nimblefix.core.Complaint;
import com.nimblefix.core.InventoryItem;
import com.nimblefix.core.Worker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AboutComplaint implements Initializable {

    private class ListItem extends Worker{
        private int pendingJobs;
        ListItem(Worker worker, int pendingJobs){
            super(worker);
            this.pendingJobs = pendingJobs;
        }

        public int getPendingJobs() {
            return pendingJobs;
        }

        public void setPendingJobs(int pendingJobs) {
            this.pendingJobs = pendingJobs;
        }
    }

    @FXML Label cID,cTS,iTitle,iCat,iID;
    @FXML TextArea cUR;
    @FXML TableView emp_Table;
    @FXML TableColumn empID,fName,email,pTask,designation;

    Complaint complaint;
    InventoryItem inventory;
    ArrayList<Category> categories;
    ArrayList<Worker> employees = new ArrayList<Worker>();
    Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        empID.setCellValueFactory(new PropertyValueFactory<ListItem,String>("empID"));
        fName.setCellValueFactory(new PropertyValueFactory<ListItem,String>("name"));
        email.setCellValueFactory(new PropertyValueFactory<ListItem,String>("email"));
        designation.setCellValueFactory(new PropertyValueFactory<ListItem,String>("designation"));
        pTask.setCellValueFactory(new PropertyValueFactory<ListItem,String>("pendingJobs"));
    }

    public void setParam(Client client, Complaint complaint, InventoryItem inventoryItem, ArrayList<Category> categories){
        this.complaint = complaint;
        this.inventory = inventoryItem;
        this.categories = categories;
        this.client = client;
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
            fetchWorkers();
        })).start();
    }

    public void setEmployees(ArrayList<Worker> employees) {
        this.employees = employees;
        populate();
    }

    private void populate() {
        emp_Table.getItems().clear();
        for(Worker w : employees){
            emp_Table.getItems().add(w);
        }
    }

    private void fetchWorkers() {
        WorkerExchangeMessage wem = new WorkerExchangeMessage(client.clientID,complaint.getOrganizationID(),new ArrayList<Worker>());
        wem.setBody("FETCH");

        try{
            client.WRITER.reset();
            client.WRITER.writeUnshared(wem);
        }catch (Exception e){ }
    }
}
