package com.nimblefix;

import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.ControlMessages.MaintainenceMessage;
import com.nimblefix.ControlMessages.PendingWorkMessage;
import com.nimblefix.ControlMessages.WorkerExchangeMessage;
import com.nimblefix.core.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class AboutFailedMaintainence implements Initializable {

    public class EmpItem extends Worker {
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

    @FXML
    Label dueSince,dueBy,iTitle,iCat,iID,aTo,aTS;
    @FXML
    TextArea admin_remarks;
    @FXML
    TableView emp_Table;
    @FXML
    TableColumn empID,fName,email,designation,pTask;
    @FXML
    TextField search_box;

    InventoryMaintainenceClass maintainence;
    MaintainenceAssignedData assignedData;
    InventoryItem inventory;
    OrganizationalFloors floor;
    ArrayList<Category> categories;
    ArrayList<Worker> employees = new ArrayList<Worker>();
    HashMap<String, Integer> pendingTaskNumbers;
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

    public void setParam(Client client, InventoryMaintainenceClass maintainence, MaintainenceAssignedData assignedData, InventoryItem inventoryItem, OrganizationalFloors floor, ArrayList<Category> categories){
        this.maintainence = maintainence;
        this.assignedData = assignedData;
        this.inventory = inventoryItem;
        this.categories = categories;
        this.floor = floor;
        this.client = client;
    }

    public void init() {
        new Thread(() -> Platform.runLater(()->{
            dueSince.setText(maintainence.getLastMaintainenceDate());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            sdf.setTimeZone(TimeZone.getDefault());

            dueBy.setText("");
            iTitle.setText(inventory.getTitle());
            iID.setText(inventory.getId());

            if(assignedData!=null) {
                aTo.setText(assignedData.getAssignedTo());
                aTS.setText(assignedData.getAssignedDateTime());
            }
            else{
                aTo.setText("Not assigned");
                aTS.setText("N/A");
            }

            for(Category c : categories) {
                if (c.getUniqueID().equals(inventory.getCategoryTag())) {
                    iCat.setText(c.getCategoryString());
                    break;
                }
            }
            fetchWorkers();
            getPendingTasks();
        })).start();
    }

    private void getPendingTasks() {
        PendingWorkMessage pm = new PendingWorkMessage(maintainence.getOUI());
        pm.setBody("FETCH");

        try{
            client.WRITER.reset();
            client.WRITER.writeUnshared(pm);
        }catch (Exception e){ }
    }

    ArrayList<Worker> tempEmployees;
    public void setPendingTask(HashMap<String,Integer> tasks){
        this.pendingTaskNumbers = tasks;
        for(Worker w : tempEmployees)
            this.employees.add(new AboutFailedMaintainence.EmpItem(w,pendingTaskNumbers.get(w.getEmail())==null?0:pendingTaskNumbers.get(w.getEmail())));
        populate();
    }

    public void setEmployees(ArrayList<Worker> employees) {
        this.tempEmployees = employees;
    }

    private void populate() {
        emp_Table.getItems().clear();
        for(Worker w : employees){
            emp_Table.getItems().add(w);
        }
    }

    private void fetchWorkers() {
        WorkerExchangeMessage wem = new WorkerExchangeMessage(client.clientID,maintainence.getOUI(),new ArrayList<Worker>());
        wem.setBody("FETCH");

        try{
            client.WRITER.reset();
            client.WRITER.writeUnshared(wem);
        }catch (Exception e){ }
    }

    public void assign_clicked(MouseEvent mouseEvent) {
        if(emp_Table.getSelectionModel().getSelectedItem()==null)return;
        if(assignedData.getAssignedTo()==null){
            Alert a = new Alert(Alert.AlertType.WARNING ,null, ButtonType.YES,ButtonType.NO);
            a.setHeaderText("Complaint is already assigned. Do you want to reassign ?");
            a.setTitle("Warning");
            a.showAndWait();
            if(a.getResult()==ButtonType.YES);
            else return;
        }


        assignedData.setOui(inventory.getParentOrganization().getOui());
        assignedData.setInventoryItem(new CompactInventoryItem(inventory.getId(),inventory.getTitle(),inventory.getParentOrganization().getOrganization_Name()));
        assignedData.setAssignedBy(client.clientID);
        assignedData.setAssignedDateTime(InventoryMaintainenceClass.getDTString(new Date()));
        assignedData.setAssignedTo(((Worker) emp_Table.getSelectionModel().getSelectedItem()).getEmail());
        assignedData.setFloorID(floor.getFloorID());
        assignedData.setAdminComments(admin_remarks.getText().trim().length()==0?"No remarks":admin_remarks.getText().trim());


        MaintainenceMessage maintainenceMessage = new MaintainenceMessage(inventory.getParentOrganization().getOui(),null);
        maintainenceMessage.setBody("MAINTAINENCE_ASSIGNMENT");

        byte[] scaled = null;
        try {
            scaled = getScaledMap(inventory, floor);
        }catch (Exception e){ e.printStackTrace(); }

        maintainenceMessage.setLocation_image(scaled);
        Map<String,MaintainenceAssignedData> m = new HashMap<String, MaintainenceAssignedData>();
        m.put(inventory.getId(),assignedData);
        maintainenceMessage.setAssignedData(m);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    client.WRITER.reset();
                    client.WRITER.writeUnshared(maintainenceMessage);
                }catch (Exception e){ }
            }
        }).start();
    }

    private byte[] getScaledMap(InventoryItem inventoryItem, OrganizationalFloors floor) throws IOException {
        Canvas canvas = new Canvas();
        Image image = SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(floor.getBackground_map())),null);

        canvas.setHeight(image.getHeight());
        canvas.setWidth(image.getWidth());

        GraphicsContext painter = canvas.getGraphicsContext2D();
        painter.drawImage(image,0,0);

        painter.setFill(Color.valueOf("#d41616"));
        painter.fillOval(inventoryItem.getLocation().getX() - 5, inventoryItem.getLocation().getY() - 5, 10, 10);
        painter.setStroke(Color.valueOf("#d41616"));
        painter.setLineWidth(2);
        painter.strokeOval(inventoryItem.getLocation().getX() - 8, inventoryItem.getLocation().getY() - 8, 16, 16);

        Image i = canvas.snapshot(new SnapshotParameters(),null);
        BufferedImage bi = SwingFXUtils.fromFXImage(i,null);
        double scale = image.getHeight()/800;

        BufferedImage scaledBI = new BufferedImage((int) (image.getWidth()/scale),(int) (image.getHeight()/scale), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(1/scale, 1/scale);
        new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC).filter(bi, scaledBI);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(scaledBI,"png",baos);
        return baos.toByteArray();
    }

    public void onAssignmentreply(MaintainenceMessage reply){
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
