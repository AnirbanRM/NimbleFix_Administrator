package com.nimblefix;

import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.core.Complaint;
import com.nimblefix.core.InventoryItem;
import com.nimblefix.core.Organization;
import com.nimblefix.core.OrganizationalFloors;
import com.sun.scenario.effect.Flood;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.font.FontFamily;

import javax.annotation.processing.SupportedSourceVersion;
import javax.imageio.ImageIO;
import javax.jws.Oneway;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Spectator implements Initializable {

    private class ComplaintListItem{

        String floorID;
        Complaint complaint;
        InventoryItem inventoryItem;
        ListCell<ComplaintListItem> cell;

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

        ListCell<FloorListItem> cell;

        int problems;
        OrganizationalFloors floor;

        FloorListItem(OrganizationalFloors floor) {
            this.floor = floor;
        }

        public void setProblems(int problems) {
            this.problems = problems;
            if(problems==0)
                ((Circle) ((Pane)cell.getGraphic()).getChildrenUnmodifiable().get(2)).setStyle("-fx-fill:#00ff00");
            else
                ((Circle) ((Pane)cell.getGraphic()).getChildrenUnmodifiable().get(2)).setStyle("-fx-fill:#ff0000");
            ((Label) ((Pane)cell.getGraphic()).getChildrenUnmodifiable().get(1)).setText(String.valueOf(problems));
        }
    }

    public Stage curr_stg;
    public Client client;
    public Organization current_organization;

    OrganizationalFloors current_selected_floor=null;
    Image floor_background_image = null;

    @FXML ListView complaintlistview, floorlistview;
    @FXML Canvas canvas;
    @FXML Label oui_box, organization_name_box,floor_id_box;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareComplaintListView();
        prepareFloorListView();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refresh_floor_list();
                redraw();
            }
        });
    }

    private void prepareFloorListView() {
        floorlistview.setCellFactory(param -> new ListCell<Spectator.FloorListItem>() {

            @Override
            protected void updateItem(Spectator.FloorListItem item, boolean empty) {
                super.updateItem(item, empty);
                setBackground(new Background(new BackgroundFill(Color.valueOf("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
                if (!empty) {
                    setBackground(new Background(new BackgroundFill(Color.valueOf("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
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
                    problems.setText("0");

                    Circle c = new Circle(4,Color.valueOf("#00ff00"));
                    c.setLayoutX(155);
                    c.setLayoutY(10);

                    item.cell = this;
                    p.getChildren().addAll(floorLabel,problems,c);
                    setGraphic(p);

                } else {
                    setOnMouseClicked(null);
                    setGraphic(null);
                    setBackground(new Background(new BackgroundFill(Color.valueOf("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setBorder(new Border(new BorderStroke(null, null, null, null)));
                }
            }
        });

        floorlistview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue==null)return;
                if(oldValue!=null)
                    ((FloorListItem)oldValue).cell.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffffff"),CornerRadii.EMPTY,Insets.EMPTY)));
                ((FloorListItem)newValue).cell.setBackground(new Background(new BackgroundFill(Color.valueOf("#ddddee"),CornerRadii.EMPTY,Insets.EMPTY)));


                set_current_floor(((Label) ((Pane)(((FloorListItem) newValue).cell.getGraphic())).getChildrenUnmodifiable().get(0)).getText());
            }
        });

    }

    private void set_current_floor(String floor) {
        current_selected_floor = this.current_organization.getFloor(floor);
        floor_id_box.setText(current_selected_floor.getFloorID());

        floor_background_image = null;
        if(current_selected_floor.getBackground_map()!=null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(current_selected_floor.getBackground_map());
            try {
                floor_background_image = SwingFXUtils.toFXImage(ImageIO.read(bis), null);
            }catch(Exception e){}
        }

        redraw();
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

                    item.cell=this;
                    p.getChildren().addAll(floorLabel,invIDLabel,commplDateLabel);
                    setGraphic(p);
                } else {
                    setGraphic(null);
                    setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setBorder(new Border(new BorderStroke(null, null, null, null)));
                }
            }
        });

        complaintlistview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(oldValue!=null)
                    ((ComplaintListItem)oldValue).cell.setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"),CornerRadii.EMPTY,Insets.EMPTY)));
                ((ComplaintListItem)newValue).cell.setBackground(new Background(new BackgroundFill(Color.valueOf("#ddddee"),CornerRadii.EMPTY,Insets.EMPTY)));
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
        this.current_organization = organization;
        organization_name_box.setText(current_organization.getOrganization_Name());
        oui_box.setText(current_organization.getOui());
    }

    private void refresh_floor_list() {
        floorlistview.getItems().clear();
        for(OrganizationalFloors f : current_organization.getFloors()){
            floorlistview.getItems().add(new FloorListItem(f));
        }
    }

    private void redraw(){
        GraphicsContext painter = canvas.getGraphicsContext2D();
        painter.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        if(floor_background_image!=null) {
            canvas.setHeight(floor_background_image.getHeight());
            canvas.setWidth(floor_background_image.getWidth());
            painter.drawImage(floor_background_image, 0, 0);
        }

        if(current_selected_floor!=null) {
            ConcurrentHashMap<String,InventoryItem> inventories = current_selected_floor.getInventories();
            for (String invKey : inventories.keySet()) {
                painter.setFill(Color.valueOf("#00bd56"));
                painter.fillOval(inventories.get(invKey).getLocation().getX() - 5, inventories.get(invKey).getLocation().getY() - 5, 10, 10);
                painter.setStroke(Color.valueOf("#00bd56"));
                painter.setLineWidth(2);
                painter.strokeOval(inventories.get(invKey).getLocation().getX() - 8, inventories.get(invKey).getLocation().getY() - 8, 16, 16);
            }
        }
        else{
            painter.setFill(Color.valueOf("#000000"));
            painter.fillText("Select a floor to view.",canvas.getWidth()/2-40,canvas.getHeight()/2);
        }
    }
}
