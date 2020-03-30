package com.nimblefix;

import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.core.*;
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
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
import java.util.ArrayList;
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
    @FXML AnchorPane canvas_field;
    Pane info_pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareComplaintListView();
        prepareFloorListView();
        canvas.setOnMouseMoved(canvashovered);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refresh_floor_list();
                redraw();

                info_pane = new Pane();
                info_pane.setMinWidth(300);
                info_pane.setMinHeight(100);
                info_pane.setLayoutX(100);
                info_pane.setLayoutY(100);
                info_pane.setBorder(new Border(new BorderStroke(Color.valueOf("#bcbcbc"),BorderStrokeStyle.SOLID, new CornerRadii(10) ,BorderWidths.DEFAULT,Insets.EMPTY)));
                info_pane.setEffect(new DropShadow(2, Color.valueOf("#aaaaaa")));

                Label title = new Label();
                title.setFont(Font.font(null,FontWeight.BOLD,15));
                title.setTextFill(Color.valueOf("#ffffff"));
                title.setText("Title");
                title.setMaxWidth(270);
                title.setLayoutX(10);
                title.setLayoutY(10);

                Label category = new Label();
                category.setFont(Font.font(null,FontWeight.SEMI_BOLD,12));
                category.setTextFill(Color.valueOf("#ffffff"));
                category.setText("Category");
                category.setMaxWidth(270);
                category.setLayoutX(10);
                category.setLayoutY(30);

                Label desc = new Label();
                desc.setFont(Font.font(null,FontWeight.NORMAL,12));
                desc.setTextFill(Color.valueOf("#ffffff"));
                desc.setText("Description");
                desc.setLayoutX(10);
                desc.setLayoutY(50);
                desc.setWrapText(true);
                desc.setMaxHeight(50);
                desc.setMaxWidth(270);
                desc.setText("Description");

                info_pane.getChildren().addAll(title,category,desc);
            }
        });
    }

    InventoryItem[][] currentFloorInventoryPoints;
    InventoryItem currentHoveringInventory = null;
    private void generatePointsMap(OrganizationalFloors organizationalFloor){
        if(floor_background_image!=null)
            currentFloorInventoryPoints = new InventoryItem[(int)floor_background_image.getHeight()][(int)floor_background_image.getWidth()];
        else
            currentFloorInventoryPoints = new InventoryItem[620][1000];
        int radius = 5;
        for(InventoryItem i : organizationalFloor.getInventories().values()){

            //Runs with O(1)
            for(int z = (int)i.getLocation().getY()-radius; z<=(int)i.getLocation().getY()+radius; z++)
                for(int zz = (int)i.getLocation().getX()-radius; zz<=(int)i.getLocation().getX()+radius; zz++)
                    try{
                        currentFloorInventoryPoints[z][zz] = i;
                    }catch (ArrayIndexOutOfBoundsException e){ }
        }
    }

    EventHandler<MouseEvent> canvashovered = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (currentFloorInventoryPoints == null)
                return;

            InventoryItem i = currentFloorInventoryPoints[(int) event.getY()][(int) event.getX()];
            if (i != null) {
                if (i.equals(currentHoveringInventory)) return;
                currentHoveringInventory = i;

            } else {
                currentHoveringInventory = null;
                canvas_field.getChildren().removeAll(info_pane);
                return;
            }

            info_pane.setLayoutX(event.getX()+10);
            info_pane.setLayoutY(event.getY()+10);

            ((Label)info_pane.getChildren().get(0)).setText(i.getTitle());
            String category = "No category";
            for(Category c : current_organization.getCategories())
                if(c.getUniqueID().equals(i.getCategoryTag()))
                    category = c.getCategoryString();
            ((Label)info_pane.getChildren().get(1)).setText(category);
            ((Label)info_pane.getChildren().get(2)).setText(i.getDescription());
            if(isOK(i))
                info_pane.setBackground(new Background(new BackgroundFill(Color.valueOf("#2C9F5E") ,new CornerRadii(10) ,Insets.EMPTY )));
            else
                info_pane.setBackground(new Background(new BackgroundFill(Color.valueOf("#d41616") ,new CornerRadii(10) ,Insets.EMPTY )));

            canvas_field.getChildren().addAll(info_pane);
        }
    };

    ArrayList<InventoryItem> defectiveitems = new ArrayList<InventoryItem>();
    private boolean isOK(InventoryItem i){
        if(defectiveitems.contains(i))
            return false;
        else
            return true;
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

        generatePointsMap(this.current_organization.getFloor(floor));
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
        String floorid=null;
        InventoryItem item=null;
        for(OrganizationalFloors of : current_organization.getFloors()){
            item = of.getInventories().get(complaint.getComplaint().getInventoryID());
            if(item!=null) {
                floorid = of.getFloorID();
                break;
            }
        }

        if(item==null||floorid==null)return;

        final InventoryItem temp1 = item;
        final String temp2=floorid;
        Platform.runLater(() -> { complaintlistview.getItems().add(new ComplaintListItem(temp2,complaint.getComplaint(),temp1)); });

        for(Object floorListItem : floorlistview.getItems()){
            if( ((Label)(((Pane) ((FloorListItem)floorListItem).cell.getGraphic()).getChildrenUnmodifiable().get(0))).getText().equals(floorid) )
                Platform.runLater(() -> {
                    ((FloorListItem)floorListItem).setProblems(((FloorListItem)floorListItem).problems+1);
                });
        }

        if(isOK(item))
            defectiveitems.add(item);
        if(current_selected_floor!=null && current_selected_floor.equals(current_organization.getFloor(floorid)))
            redraw();
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
                String color;
                if(isOK(inventories.get(invKey)))
                    color = "#00bd56";
                else
                    color = "#d41616";
                painter.setFill(Color.valueOf(color));
                painter.fillOval(inventories.get(invKey).getLocation().getX() - 5, inventories.get(invKey).getLocation().getY() - 5, 10, 10);
                painter.setStroke(Color.valueOf(color));
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
