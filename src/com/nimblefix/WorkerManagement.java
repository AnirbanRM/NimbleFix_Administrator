package com.nimblefix;

import com.nimblefix.core.Category;
import com.nimblefix.core.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WorkerManagement implements Initializable {

    public Stage curr_stg;
    public Client client;
    ArrayList<Worker> workers;

    @FXML TableView employeeTable;
    @FXML TableColumn empID,fName,email,mobile,designation,dob,doj;

    public void add_clicked(MouseEvent mouseEvent) throws Exception{
        Worker w = new Worker();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutWorkerUI.fxml"));
        Parent root = loader.load();
        Stage primaryStage= new Stage();
        primaryStage.setTitle("Worker Management");
        primaryStage.setScene(new Scene(root, 570, 250));
        primaryStage.setResizable(false);

        ArrayList<String> desigs = new ArrayList<String>();
        for(Worker worker : workers)
            if(!desigs.contains(worker.getDesignation()))
                desigs.add(worker.getDesignation());

        Worker temporaryWorker = (Worker) w.clone();
        ((AboutWorker)(loader.getController())).setWorker(temporaryWorker,desigs);
        ((AboutWorker)loader.getController()).curr_stg=primaryStage;

        primaryStage.initOwner(curr_stg);
        primaryStage.showAndWait();

        if(temporaryWorker!=null) {
            workers.add(temporaryWorker);
            rePopulate();
        }
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

        empID.setCellValueFactory(new PropertyValueFactory<Worker,String>("empID"));
        fName.setCellValueFactory(new PropertyValueFactory<Worker,String>("name"));
        email.setCellValueFactory(new PropertyValueFactory<Worker,String>("email"));
        mobile.setCellValueFactory(new PropertyValueFactory<Worker,String>("mobile"));
        designation.setCellValueFactory(new PropertyValueFactory<Worker,String>("designation"));
        dob.setCellValueFactory(new PropertyValueFactory<Worker,String>("doB"));
        doj.setCellValueFactory(new PropertyValueFactory<Worker,String>("doJ"));
    }
}
