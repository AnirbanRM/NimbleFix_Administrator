package com.nimblefix;

import com.nimblefix.ControlMessages.WorkerExchangeMessage;
import com.nimblefix.core.Worker;
import com.nimblefix.core.WorkerFilter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class WorkerManagement implements Initializable {

    public Stage curr_stg;
    public Client client;
    static ArrayList<Worker> workers;
    String organizationID,organizationName;

    @FXML TableView employeeTable;
    @FXML TableColumn empID,fName,email,mobile,designation,dob,doj;
    @FXML Label orgID,orgName;

    @FXML TextField emp_ids,emp_idm1,emp_idm2,namebox,emailbox,mob_nobox;
    @FXML DatePicker dobs,dojs,dobm1,dobm2,dojm1,dojm2;
    @FXML ChoiceBox designationbox;
    @FXML TitledPane filter_box;

    @FXML ToggleGroup G1,G2,G3;

    public void add_clicked(MouseEvent mouseEvent) throws Exception{
         launchAboutWorker(null);
         filter_clicked(null);
    }

    private void launchAboutWorker(Worker w) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutWorkerUI.fxml"));
        Parent root = loader.load();
        Stage primaryStage= new Stage();
        primaryStage.setTitle("Worker Management");
        primaryStage.setScene(new Scene(root, 570, 250));
        primaryStage.setResizable(false);

        ArrayList<String> desigs = new ArrayList<String>();
        for(Worker worker : workers)
            if(!desigs.contains(worker.getDesignation()))
                desigs.add(worker.getDesignation());;

        ((AboutWorker)(loader.getController())).setWorker(w,desigs);
        ((AboutWorker)loader.getController()).curr_stg=primaryStage;

        primaryStage.initOwner(curr_stg);
        primaryStage.showAndWait();
    }

    private void rePopulate() {
        filter_clicked(null);
    }

    private void rePopulate(ArrayList<Worker> filteredWorkers) {
        employeeTable.getItems().clear();
        for(Worker w : workers)
            if(filteredWorkers.contains(w))
                employeeTable.getItems().add(w);
    }

    public void remove_clicked(MouseEvent mouseEvent){
        int index = employeeTable.getSelectionModel().getSelectedIndex();
        if(index!=-1) {
            workers.remove((Worker) employeeTable.getSelectionModel().getSelectedItem());
            filter_clicked(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workers = new ArrayList<Worker>();

        Platform.runLater(()->{
            orgID.setText(organizationID);
            orgName.setText(organizationName);
        });

        employeeTable.setRowFactory(new Callback<TableView, TableRow>() {
            @Override
            public TableRow call(TableView param) {
                TableRow tr = new TableRow();
                tr.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount()==2 && !event.getTarget().toString().contains("null") ){
                            if(employeeTable.getSelectionModel().getSelectedItem()==null)return;
                            try {
                                launchAboutWorker(((Worker) employeeTable.getSelectionModel().getSelectedItem()));
                                rePopulate();
                            } catch (Exception e) { }
                        }
                    }
                });
                return tr;
            }
        });

        empID.setCellValueFactory(new PropertyValueFactory<Worker,String>("empID"));
        fName.setCellValueFactory(new PropertyValueFactory<Worker,String>("name"));
        email.setCellValueFactory(new PropertyValueFactory<Worker,String>("email"));
        mobile.setCellValueFactory(new PropertyValueFactory<Worker,String>("mobile"));
        designation.setCellValueFactory(new PropertyValueFactory<Worker,String>("designation"));
        dob.setCellValueFactory(new PropertyValueFactory<Worker,String>("doB"));
        doj.setCellValueFactory(new PropertyValueFactory<Worker,String>("doJ"));

        Platform.runLater(()->{ fetchWorkers(); });

        filter_box.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) filter_box.setPrefHeight(510);
            else filter_box.setPrefHeight(0);
        });
    }

    private void fetchWorkers() {
        new Thread(() -> {
            WorkerExchangeMessage wem = new WorkerExchangeMessage(client.clientID,organizationID,workers);
            wem.setBody("FETCH");

            try{
                client.WRITER.reset();
                client.WRITER.writeUnshared(wem);
            }catch (Exception e){ }
            final WorkerExchangeMessage workerExchangeMessage = (WorkerExchangeMessage) client.readNext();
            if(workerExchangeMessage.getBody().equals("FETCHRESULT")){
                ArrayList<String> designations = new ArrayList<String>();
                for(Worker w : workerExchangeMessage.getWorkers()) {
                    workers.add(w);
                    if(!designations.contains(w.getDesignation()))
                        designations.add(w.getDesignation());
                }
                Platform.runLater(()->{
                    Collections.sort(designations);
                    designationbox.getItems().add("Any Designation");
                    for(String desig : designations)
                        designationbox.getItems().add(desig);
                    designationbox.getSelectionModel().select(0);
                    rePopulate();
                });
            }
        }).start();
    }

    public void saveClicked(MouseEvent mouseEvent) {
        WorkerExchangeMessage wem = new WorkerExchangeMessage(client.clientID,organizationID,workers);
        wem.setBody("UPDATE");

        try{
            client.WRITER.reset();
            client.WRITER.writeUnshared(wem);
        }catch (Exception e){ }

        wem = (WorkerExchangeMessage) client.readNext();
        if(wem.getBody().equals("SUCCESS")){
            Alert a = new Alert(Alert.AlertType.INFORMATION ,null, ButtonType.OK);
            a.setHeaderText("Workers updated successfully.");
            a.setTitle("Success");
            a.showAndWait();
        }
    }

    public void close_clicked(MouseEvent mouseEvent) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("DashboardUI.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Dashboard");
        stage.setResizable(false);
        stage.setScene(new Scene(root, 800, 500));
        ((Dashboard)loader.getController()).curr_stg=stage;

        StringBuilder sb = new StringBuilder();
        for(char i : client.clientSocket.getRemoteSocketAddress().toString().substring(1).toCharArray())
            if(i==':')break;
            else
                sb.append(i);

        ((Dashboard)loader.getController()).setAddressandUserandClient(sb.toString(),client.clientID,client);

        client.getCurrentShowingStage().hide();
        client.setCurrentShowingStage(stage);

        stage.show();
    }

    public void filter_clicked(MouseEvent mouseEvent) {
        WorkerFilter workerFilter = new WorkerFilter(workers);

        if (((RadioButton) G1.getSelectedToggle()).getText().equals("contains"))
            workerFilter.filterByEmpID(emp_ids.getText());
        else if (((RadioButton) G1.getSelectedToggle()).getText().equals("between"))
            workerFilter.filterByEmpID(emp_idm1.getText(), emp_idm2.getText());

        workerFilter.filterByDesignation(designationbox.getSelectionModel().getSelectedItem().toString());

        workerFilter.filterByEmail(emailbox.getText());

        workerFilter.filterByMobile(mob_nobox.getText());

        workerFilter.filterByName(namebox.getText());

        if (((RadioButton) G2.getSelectedToggle()).getText().equals("is") && dobs.getValue()!=null)
            workerFilter.filterByDOB(dobs.getValue().toString());
        else if (((RadioButton) G2.getSelectedToggle()).getText().equals("between") && dobm1.getValue()!=null && dobm2.getValue()!=null)
            workerFilter.filterByDOB(dobm1.getValue().toString(), dobm2.getValue().toString());

        if (((RadioButton) G3.getSelectedToggle()).getText().equals("is") && dojs.getValue()!=null)
            workerFilter.filterByDOJ(dojs.getValue().toString());
        else if (((RadioButton) G3.getSelectedToggle()).getText().equals("between") && dojm1.getValue()!=null && dojm2.getValue()!=null)
            workerFilter.filterByDOJ(dojm1.getValue().toString(), dojm2.getValue().toString());

        rePopulate(workerFilter.getFilteredResult());
    }
}