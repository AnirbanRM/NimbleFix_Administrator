package com.nimblefix;

import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.core.Complaint;
import com.nimblefix.core.InventoryItem;
import com.nimblefix.core.Organization;
import com.nimblefix.core.OrganizationalFloors;
import com.sun.scenario.effect.Flood;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.font.FontFamily;

import javax.jws.Oneway;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Spectator implements Initializable {

    private class ComplaintListItem{

        String floorID;
        Complaint complaint;
        InventoryItem inventoryItem;

        ComplaintListItem(String floorID,Complaint complaint,InventoryItem inventoryItem){
            this.complaint = complaint;
            this.floorID = floorID;
            this.inventoryItem = inventoryItem;
        }

        public String getFloorID() {
            return floorID;
        }

        public Complaint getComplaint() {
            return complaint;
        }

        public InventoryItem getInventoryItem() {
            return inventoryItem;
        }

        public void setFloorID(String floorID) {
            this.floorID = floorID;
        }

        public void setComplaint(Complaint complaint) {
            this.complaint = complaint;
        }

        public void setInventoryItem(InventoryItem inventoryItem) {
            this.inventoryItem = inventoryItem;
        }
    }

    private class FloorListItem {

        int problems;
        OrganizationalFloors floor;

        FloorListItem(OrganizationalFloors floor) {
            this.floor = floor;
        }

        public FloorListItem setProblems(int problems) {
            this.problems = problems;
            return this;
        }

        public int getProblems() {
            return problems;
        }
    }

    public Stage curr_stg;
    public Client client;

    @FXML ListView complaintlistview,floorlistview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareComplaintListView();
        prepareFloorListView();
        

        complaintlistview.getItems().add(new ComplaintListItem("FID",new Complaint() ,new InventoryItem(new Organization("organ") ,"454545454528","MACBOOK","LOL",1,1)));
        complaintlistview.getItems().add(new ComplaintListItem("FID",new Complaint() ,new InventoryItem(new Organization("organ") ,"454545454528","MACBOOK","LOL",1,1)));
        complaintlistview.getItems().add(new ComplaintListItem("FID",new Complaint() ,new InventoryItem(new Organization("organ") ,"454545454528","MACBOOK","LOL",1,1)));
        complaintlistview.getItems().add(new ComplaintListItem("FID",new Complaint() ,new InventoryItem(new Organization("organ") ,"454545454528","MACBOOK","LOL",1,1)));
        complaintlistview.getItems().add(new ComplaintListItem("FID",new Complaint() ,new InventoryItem(new Organization("organ") ,"454545454528","MACBOOK","LOL",1,1)));

        floorlistview.getItems().add(new FloorListItem(new OrganizationalFloors("GROUND")).setProblems(1));
        floorlistview.getItems().add(new FloorListItem(new OrganizationalFloors("1st")).setProblems(2));
        floorlistview.getItems().add(new FloorListItem(new OrganizationalFloors("2nd")).setProblems(0));
        floorlistview.getItems().add(new FloorListItem(new OrganizationalFloors("3rd")).setProblems(3));

    }

    private void prepareFloorListView() {
        floorlistview.setCellFactory(param -> new ListCell<Spectator.FloorListItem>() {
            @Override
            protected void updateItem(Spectator.FloorListItem item, boolean empty) {
                super.updateItem(item, empty);
                setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                if (!empty) {
                    setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setBorder(new Border(new BorderStroke(null, null,Color.valueOf("#bcbcbc"), null, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, new CornerRadii(0), BorderWidths.DEFAULT, Insets.EMPTY)));

                    setPrefHeight(50);
                    Pane p = new Pane();

                    Label floorLabel = new Label();
                    floorLabel.setLayoutX(5);
                    floorLabel.setLayoutY(15);
                    floorLabel.setFont(Font.font(null, FontWeight.BOLD, 15));

                    floorLabel.setText(item.floor.getFloorID());

                    Label problems = new Label();
                    problems.setLayoutY(2);
                    problems.setLayoutX(165);
                    problems.setFont(Font.font(null, FontWeight.NORMAL, 12));
                    problems.setText(String.valueOf(item.getProblems()));

                    Circle c;
                    if(item.getProblems()==0)
                        c = new Circle(4,Color.valueOf("#00ff00"));
                    else
                        c = new Circle(4,Color.valueOf("#ff0000"));
                    c.setLayoutY(10);
                    c.setLayoutX(155);

                    p.getChildren().addAll(floorLabel,problems,c);
                    setGraphic(p);

                } else {
                    setGraphic(null);
                    setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setBorder(new Border(new BorderStroke(null, null, null, null)));
                }
            }
        });

    }

    private void prepareComplaintListView() {

        complaintlistview.setCellFactory(param -> new ListCell<Spectator.ComplaintListItem>() {
            @Override
            protected void updateItem(Spectator.ComplaintListItem item, boolean empty) {
                super.updateItem(item, empty);
                setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                if (!empty) {
                    setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setBorder(new Border(new BorderStroke(null, Color.valueOf("#bcbcbc"), null, null, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, new CornerRadii(0), BorderWidths.DEFAULT, Insets.EMPTY)));

                    setPrefWidth(250);

                    Pane p = new Pane();

                    Label floorLabel = new Label();
                    floorLabel.setLayoutX(5);
                    floorLabel.setLayoutY(2);
                    floorLabel.setFont(Font.font(null, FontWeight.BOLD, 15));

                    floorLabel.setText(item.floorID + " - "+item.inventoryItem.getTitle());

                    Label invIDLabel = new Label();
                    invIDLabel.setLayoutY(22);
                    invIDLabel.setLayoutX(5);
                    invIDLabel.setFont(Font.font(null, FontWeight.NORMAL, 10));
                    invIDLabel.setText(item.inventoryItem.getId());

                    Label commplDateLabel = new Label();
                    commplDateLabel.setText("DATE TIME");
                    commplDateLabel.setFont(Font.font(12));
                    commplDateLabel.setLayoutX(5);
                    commplDateLabel.setLayoutY(42);

                    p.getChildren().addAll(floorLabel,invIDLabel,commplDateLabel);
                    setGraphic(p);
                } else {
                    setGraphic(null);
                    setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setBorder(new Border(new BorderStroke(null, null, null, null)));
                }
            }
        });
    }


    public void startComplaintListener () {

        curr_stg.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (client.currentShowingStage.equals(curr_stg)) {
                    try {
                        client.clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread receiverthd = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Object o = null;
                    o = client.readNext();
                    if (o != null) {
                        final Object temp = o;
                        if (o instanceof ComplaintMessage) {
                            Thread complaint_thd = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handle_complaint((ComplaintMessage) temp);
                                }
                            });
                            complaint_thd.start();
                        }
                    } else
                        break;
                }
            }
        });
        receiverthd.start();
    }

    private void handle_complaint (ComplaintMessage complaint){

        System.out.println(complaint);

    }

    public void load (Organization organization){


    }
}
