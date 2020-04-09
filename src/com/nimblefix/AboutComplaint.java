package com.nimblefix;

import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.ControlMessages.WorkerExchangeMessage;
import com.nimblefix.core.Category;
import com.nimblefix.core.Complaint;
import com.nimblefix.core.InventoryItem;
import com.nimblefix.core.Worker;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class AboutComplaint implements Initializable {

    public class EmpItem extends Worker{
        private String pJobs;
        EmpItem(Worker worker,int pendingJobs){
            super(worker);
            this.pJobs = String.valueOf(pendingJobs);
        }

        public String getPJobs() {
            return pJobs;
        }

        public void setPJobs(int pendingJobs) {
            this.pJobs = String.valueOf(pendingJobs);
        }

        @Override
        public String toString(){
            return super.getEmpID()+super.getEmail()+super.getName()+super.getDesignation()+getPJobs();
        }
    }

    @FXML Label cID,cTS,iTitle,iCat,iID;
    @FXML TextArea cUR,admin_remarks;
    @FXML TableView emp_Table;
    @FXML TableColumn empID,fName,email,designation,pTask;
    @FXML TextField search_box;

    Complaint complaint;
    InventoryItem inventory;
    ArrayList<Category> categories;
    ArrayList<Worker> employees = new ArrayList<Worker>();
    Client client;
    Stage curr_stage=null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        empID.setCellValueFactory(new PropertyValueFactory<Worker,String>("empID"));
        fName.setCellValueFactory(new PropertyValueFactory<Worker,String>("name"));
        email.setCellValueFactory(new PropertyValueFactory<Worker,String>("email"));
        pTask.setCellValueFactory(new PropertyValueFactory<Worker,String>("pJobs"));
        designation.setCellValueFactory(new PropertyValueFactory<Worker,String>("designation"));

        search_box.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                emp_Table.getItems().clear();
                Iterator<Worker> iterator = employees.iterator();
                while(iterator.hasNext()){
                    Worker w = iterator.next();
                    if(w.toString().toLowerCase().contains(newValue.toLowerCase()))
                        emp_Table.getItems().add(w);
                }
            }
        });
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
        for(Worker w : employees)
            this.employees.add(new EmpItem(w,0));
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

    public void assign_clicked(MouseEvent mouseEvent) {
        if(emp_Table.getSelectionModel().getSelectedItem()==null)return;
        complaint.setAdminComments(admin_remarks.getText().trim().length()==0?"No remarks":admin_remarks.getText().trim());
        complaint.setAssignedBy(client.clientID);
        complaint.setAssignedDate(Complaint.getDTString(new Date()));
        complaint.setAssignedTo(((Worker) emp_Table.getSelectionModel().getSelectedItem()).getEmail());

        ComplaintMessage complaintMessage = new ComplaintMessage(complaint);
        complaintMessage.setBody("ASSIGNMENT");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    client.WRITER.reset();
                    client.WRITER.writeUnshared(complaintMessage);
                }catch (Exception e){ }
            }
        }).start();
    }

    public void onAssignmentreply(ComplaintMessage reply){
        Platform.runLater(()->{
            if(reply.getBody().equals("ASSIGNMENT_SUCCESS")){
                Alert a = new Alert(Alert.AlertType.INFORMATION ,null, ButtonType.OK);
                a.setHeaderText("Worker successfully assigned.");
                a.setTitle("Success");
                a.showAndWait();
                curr_stage.close();
            }else{
                Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
                a.setHeaderText("Cannot assign worker. Please retry.");
                a.setTitle("Error");
                a.showAndWait();
            }
        });
    }
}