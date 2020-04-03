package com.nimblefix;

import com.nimblefix.core.Category;
import com.nimblefix.core.Worker;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
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

import java.lang.annotation.Target;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WorkerManagement implements Initializable {

    public Stage curr_stg;
    public Client client;
    static ArrayList<Worker> workers;
    String organizationID,organizationName;

    @FXML TableView employeeTable;
    @FXML TableColumn empID,fName,email,mobile,designation,dob,doj;
    @FXML Label orgID,orgName;

    public void add_clicked(MouseEvent mouseEvent) throws Exception{
         launchAboutWorker(null);
         rePopulate();
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
        employeeTable.getItems().clear();
        for(Worker w : workers)
            employeeTable.getItems().add(w);
    }

    public void remove_clicked(MouseEvent mouseEvent){
        int index = employeeTable.getSelectionModel().getSelectedIndex();
        if(index!=-1) {
            workers.remove(index);
            rePopulate();
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
    }
}