package com.nimblefix;

import com.nimblefix.core.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.time.*;
import java.util.*;

public class InventoryHistory implements Initializable, EventHandler<MouseEvent>, ChangeListener {

    Client client;
    Stage curr_stg;
    ArrayList<Category> categories;
    ArrayList<OrganizationalFloors> floors;
    String organizationID;
    String organizationName;

    Map<String, List<InventoryItemHistory>> histories = new HashMap<String, List<InventoryItemHistory>>();
    Map<String, InventoryMaintainenceClass> maintainenceMap = new HashMap<String, InventoryMaintainenceClass>();

    @FXML Canvas calendar;
    @FXML Canvas month_header;
    @FXML Canvas date_header;
    @FXML Canvas overlay_canvas;

    @FXML AnchorPane monthPane;
    @FXML AnchorPane datePane;
    @FXML AnchorPane event_holder;

    @FXML ScrollPane mainViewPoint;

    @FXML Label orgName,orgID,yearbox,inventory_heading;
    @FXML ChoiceBox floordropdown;
    @FXML TextField search_box;
    @FXML TreeView inventoryTree;
    @FXML Button year_next,year_prev;

    GraphicsContext context,month_canvas_context,date_canvas_context;

    double xDIV,yDIV;
    CustomTreeNode currentNode;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        floordropdown.getSelectionModel().selectedItemProperty().addListener(this);
        search_box.textProperty().addListener(this);
        inventoryTree.getSelectionModel().selectedItemProperty().addListener(this);

        context = calendar.getGraphicsContext2D();
        month_canvas_context = month_header.getGraphicsContext2D();
        date_canvas_context = date_header.getGraphicsContext2D();

        xDIV = calendar.getWidth()/12;
        yDIV = calendar.getHeight()/31;

        Platform.runLater(()->{
            initCalendar();
        });
    }


    private void initCalendar() {
        Date d = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(d.toInstant(),ZoneId.systemDefault());
        yearbox.setText(String.valueOf(ldt.getYear()));

        mainViewPoint.vvalueProperty().addListener((observable, oldValue, newValue) -> monthPane.setTranslateY(newValue.doubleValue()*(calendar.getHeight()-mainViewPoint.getViewportBounds().getHeight()+50)));
        mainViewPoint.hvalueProperty().addListener((observable, oldValue, newValue) -> datePane.setTranslateX(newValue.doubleValue()*(calendar.getWidth()-mainViewPoint.getViewportBounds().getWidth()+50)));

        mainViewPoint.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            monthPane.setTranslateY( mainViewPoint.getVvalue()*(calendar.getHeight()-newValue.getHeight()+50));
            datePane.setTranslateX( mainViewPoint.getHvalue()*(calendar.getWidth()-newValue.getWidth()+50));

        });
        calendar.setTranslateY(50);
        calendar.setTranslateX(50);

        overlay_canvas.setTranslateX(50);
        overlay_canvas.setTranslateY(50);

        overlay_canvas.setOnMouseMoved(this);

        drawCalendar();

        setCurrentDateMarker();
    }


    private void drawCalendar() {
        month_canvas_context.setFill(Color.valueOf("#4D089A"));
        date_canvas_context.setFill(Color.valueOf("#4D089A"));

        month_canvas_context.fillRect(0,0,month_header.getWidth(),month_header.getHeight());
        date_canvas_context.fillRect(0,0,date_header.getWidth(),date_header.getHeight());

        month_canvas_context.setLineWidth(1);
        date_canvas_context.setLineWidth(1);


        //Months
        String[] months = new String[]{"January","February","March","April","May","June","July","August","September", "October","November","December"};
        month_canvas_context.setStroke(Color.valueOf("#ffffff"));
        month_canvas_context.setFill(Color.valueOf("#ffffff"));
        month_canvas_context.setFont(Font.font(null , FontWeight.BOLD,20));

        for(int i = 0; i< 12; i++) {
            month_canvas_context.fillText(months[i], i * xDIV + 15, 35);
            month_canvas_context.strokeLine(i * xDIV, 0, i * xDIV, month_header.getHeight());
        }

        //Days
        date_canvas_context.setStroke(Color.valueOf("#ffffff"));
        date_canvas_context.setFill(Color.valueOf("#ffffff"));
        date_canvas_context.setFont(Font.font(null , FontWeight.BOLD,20));

        for(int i = 0; i< 31; i++) {
            date_canvas_context.fillText(String.valueOf(i + 1), 10, yDIV * i + 60);
            date_canvas_context.strokeLine(0, i * yDIV, date_header.getWidth(), i * yDIV);
        }

        redrawGridLines();
    }

    private void redrawGridLines() {
        context.setFill(Color.valueOf("#ffffff"));
        context.fillRect(0,0,calendar.getWidth(),calendar.getHeight());

        context.setStroke(Color.valueOf("#000000"));
        context.setLineWidth(0.1);
        for(int i = 01; i<= 12; i++)
            context.strokeLine(i * xDIV, 0, i * xDIV, calendar.getHeight());

        for(int i = 01; i<= 31; i++)
            context.strokeLine(0, i * yDIV, calendar.getWidth(), i * yDIV);
    }


    private void setCurrentDateMarker(){
        GraphicsContext context = overlay_canvas.getGraphicsContext2D();
        LocalDateTime localDateTime = LocalDateTime.now();

        context.setStroke(Color.RED);
        context.setFill(Color.RED);

        double timeCorrection = ((double) (localDateTime.getHour()*60+localDateTime.getMinute())/(24*60))*yDIV;

        context.setLineWidth(1);
        context.setLineDashes(8);
        context.strokeLine(0,(yDIV*(localDateTime.getDayOfMonth()-1)) + timeCorrection,xDIV*(localDateTime.getMonthValue()-1),yDIV*(localDateTime.getDayOfMonth()-1) + timeCorrection);

        context.setLineWidth(2);
        context.setLineDashes(0);
        context.strokeLine(xDIV*(localDateTime.getMonthValue()-1),(yDIV*(localDateTime.getDayOfMonth()-1)) + timeCorrection,xDIV*(localDateTime.getMonthValue()),yDIV*(localDateTime.getDayOfMonth()-1) +timeCorrection);

        double point_radius = 5;
        context.fillOval(xDIV*(localDateTime.getMonthValue()-1)-point_radius,yDIV*(localDateTime.getDayOfMonth()-1)-point_radius + timeCorrection,2*point_radius,2*point_radius);
        context.fillOval(xDIV*localDateTime.getMonthValue()-point_radius,yDIV*(localDateTime.getDayOfMonth()-1)-point_radius + timeCorrection,2*point_radius,2*point_radius);
    }

    public void addEvent(InventoryItemHistory history){
        Pane p = new Pane();
        p.setMinHeight(yDIV-10);
        p.setMinWidth(xDIV-20);
        p.setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"),new CornerRadii(10),Insets.EMPTY)));
        ArrayList<Double> dashes = new ArrayList<>();dashes.add(new Double(2)); dashes.add(new Double(10));
        p.setBorder(new Border(new BorderStroke(null,null,null,Color.WHITE,null,null,null,new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.BEVEL , StrokeLineCap.SQUARE ,200,0, dashes) ,CornerRadii.EMPTY,new BorderWidths(0,0,0,5),Insets.EMPTY )));

        Label datetimeLabel = new Label();
        datetimeLabel.setLayoutX(20);
        datetimeLabel.setLayoutY(10);
        datetimeLabel.setFont(Font.font(null,FontWeight.BOLD,12));
        datetimeLabel.setTextFill(Color.WHITE);

        Label statusLabel = new Label();
        statusLabel.setLayoutX(20);
        statusLabel.setLayoutY(50);
        statusLabel.setFont(Font.font(null,FontWeight.BOLD,12));
        statusLabel.setTextFill(Color.WHITE);

        Label assignedtoLabel = new Label();
        assignedtoLabel.setLayoutX(20);
        assignedtoLabel.setLayoutY(65);
        assignedtoLabel.setFont(Font.font(null,FontWeight.BOLD,12));
        assignedtoLabel.setTextFill(Color.WHITE);

        p.getChildren().addAll(datetimeLabel,statusLabel,assignedtoLabel);

        Date d = InventoryItemHistory.getDTDate(history.getWorkDateTime());
        LocalDateTime dd = LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());

        p.setLayoutX((dd.getMonthValue()-1)*xDIV-2);
        p.setLayoutY((dd.getDayOfMonth()-1)*yDIV+5);

        datetimeLabel.setText(dd.getDayOfMonth()+"/"+dd.getMonthValue()+"/"+dd.getYear()+" "+ String.format("%02d",dd.getHour()) +":"+ String.format("%02d", dd.getMinute()));

        if(history.getEventType()== InventoryItemHistory.Type.REGISTERED) {
            statusLabel.setText("New Complaint Registered ");
            assignedtoLabel.setText("");
        }
        else if(history.getEventType()== InventoryItemHistory.Type.FIXED){
            statusLabel.setText("Fixed by");
            assignedtoLabel.setText(history.getAssignedTo());
        }
        else if(history.getEventType()== InventoryItemHistory.Type.ASSIGNED){
            statusLabel.setText("Assigned to");
            assignedtoLabel.setText(history.getAssignedTo());
        }
        else if(history.getEventType()== InventoryItemHistory.Type.POSTPONED) {
            statusLabel.setText("Postponed by");
            assignedtoLabel.setText(history.getAssignedTo());
        }
        else if(history.getEventType()== InventoryItemHistory.Type.FUTURE) {
            statusLabel.setText("Periodic Maintenance Schedule");
        }
        event_holder.getChildren().add(p);
    }

    //On overlay_canvas hovered
    @Override
    public void handle(MouseEvent event) {
        redrawGridLines();
        context.setFill(Color.valueOf("#ededed"));
        context.fillRect(Math.floor(event.getX()/xDIV)*xDIV,Math.floor(event.getY()/yDIV)*yDIV,xDIV,yDIV);
    }

    public void setExtra(String organizationID, String organizationName) {
        this.organizationID = organizationID;
        this.organizationName = organizationName;
        orgID.setText(organizationID);
        orgName.setText(organizationName);
    }

    public void setInventoryItems(ArrayList<Category> categories, ArrayList<OrganizationalFloors> floors) {
        this.categories = categories;
        this.floors = floors;
        setFloorItems(floors);
        setFilterInventory(search_box.getText(), floordropdown.getSelectionModel().getSelectedItem().toString());
    }

    private void setFloorItems(ArrayList<OrganizationalFloors> floors) {
        floordropdown.getItems().add("Any Floor");
        for(OrganizationalFloors f : floors)
            floordropdown.getItems().add(f.getFloorID());
        floordropdown.getSelectionModel().select(0);
    }

    private void setFilterInventory(String filterstring, String floorID){
        Map<String,CustomTreeNode<String>> categoryTree = new HashMap<>();
        CustomTreeNode<String> root = new CustomTreeNode("Inventory Items",CustomTreeNode.Type.ROOT);
        inventoryTree.setRoot(root);
        root.expandedProperty().setValue(true);

        CustomTreeNode<String> temp = null;
        for(Category i : categories){
            temp = new CustomTreeNode(i.getCategoryString(),CustomTreeNode.Type.CATEGORY,i.getUniqueID());
            categoryTree.put(i.getUniqueID(),temp);
            root.getChildren().add(temp);
        }
        temp = new CustomTreeNode("UnCategorized Items",CustomTreeNode.Type.CATEGORY);
        categoryTree.put("NONE",temp);
        root.getChildren().add(temp);

        boolean all = false;
        if(floordropdown.getSelectionModel().getSelectedIndex()==0)all = true;

        for (OrganizationalFloors f : floors) {
            if((!all && f.getFloorID().equals(floorID)) || all) {
                for (InventoryItem i : f.getInventories().values()) {
                    if(!(i.getId()+i.getTitle()).toLowerCase().contains(filterstring.toLowerCase()))continue;
                    CustomTreeNode<String> item = categoryTree.get(i.getCategoryTag());
                    if (item == null) {
                        categoryTree.get("NONE").getChildren().add(new CustomTreeNode(i.getTitle() + " (" + i.getId() + ")",CustomTreeNode.Type.ITEM,i.getId()));
                        categoryTree.get("NONE").setExpanded(true);
                    }
                    else {
                        item.getChildren().add(new CustomTreeNode(i.getTitle() + " (" + i.getId() + ")",CustomTreeNode.Type.ITEM,i.getId()));
                        item.setExpanded(true);
                    }
                }
            }
        }
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(newValue instanceof String) {
            if (newValue == null || search_box == null) return;
            setFilterInventory(search_box.getText(), floordropdown.getSelectionModel().getSelectedItem().toString());
        }
        else if(newValue instanceof CustomTreeNode){
            CustomTreeNode item = (CustomTreeNode) newValue;
            currentNode =  item;

            if(((CustomTreeNode)newValue).getType()== CustomTreeNode.Type.ITEM) {
                String heading = "";
                InventoryItem inventoryItem = null;
                for (OrganizationalFloors f : floors) {
                    if (f.getInventories().get(item.getuID()) != null) {
                        inventoryItem = f.getInventories().get(item.getuID());
                        break;
                    }
                }
                heading += inventoryItem.getTitle() + " (" + currentNode.getuID() + ") ";
                for (Category c : categories) {
                    if (c.getUniqueID().equals(inventoryItem.getCategoryTag())) {
                        heading += " - " + c.getCategoryString() + " ";
                    }
                }
                inventory_heading.setText(heading);
            }
            else
                inventory_heading.setText("No inventory item selected");

            setEvents();
        }
    }

    private void setEvents() {
        if(currentNode==null)return;
        if(currentNode.getType() == CustomTreeNode.Type.ROOT || currentNode.getType() == CustomTreeNode.Type.CATEGORY){
            event_holder.getChildren().clear();
        }

        else if(currentNode.getType()== CustomTreeNode.Type.ITEM){
            event_holder.getChildren().clear();
            List<InventoryItemHistory> hs = histories.get(currentNode.getuID());
            if(hs!=null)
                for (InventoryItemHistory hitem : hs)
                    addEvent(hitem);
                addEvent(maintainenceMap.get(currentNode.getuID()));
        }
    }

    private void addEvent(InventoryMaintainenceClass maintainenceClass) {
        if(maintainenceClass==null)return;
        for(LocalDateTime dd : maintainenceClass.getMaintainanceDates(Integer.parseInt(yearbox.getText()))) {

            Pane p = new Pane();
            p.setMinHeight(yDIV - 40);
            p.setMinWidth(xDIV - 20);
            ArrayList<Double> dashes = new ArrayList<>();
            dashes.add(new Double(2));
            dashes.add(new Double(10));
            p.setBorder(new Border(new BorderStroke(null, null, null, Color.WHITE, null, null, null, new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.BEVEL, StrokeLineCap.SQUARE, 200, 0, dashes), CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 5), Insets.EMPTY)));
            if(LocalDateTime.now().compareTo(dd)<0)
                p.setBackground(new Background(new BackgroundFill(Color.valueOf("#009ABA"), new CornerRadii(10), Insets.EMPTY)));
            else
                p.setBackground(new Background(new BackgroundFill(Color.valueOf("#DD2C00"), new CornerRadii(10), Insets.EMPTY)));

            Label datetimeLabel = new Label();
            datetimeLabel.setLayoutX(20);
            datetimeLabel.setLayoutY(10);
            datetimeLabel.setFont(Font.font(null, FontWeight.BOLD, 12));
            datetimeLabel.setTextFill(Color.WHITE);

            Label statusLabel = new Label();
            statusLabel.setLayoutX(20);
            statusLabel.setLayoutY(30);
            statusLabel.setFont(Font.font(null, FontWeight.BOLD, 12));
            statusLabel.setTextFill(Color.WHITE);
            if(LocalDateTime.now().compareTo(dd)<0)
                statusLabel.setText("Future preplanned Maintenance");
            else
                statusLabel.setText("Preplanned maintenance failed");

            p.getChildren().addAll(datetimeLabel, statusLabel);
            p.setLayoutX((dd.getMonthValue() - 1) * xDIV - 2);
            p.setLayoutY((dd.getDayOfMonth() - 1) * yDIV + 5);

            datetimeLabel.setText(dd.getDayOfMonth() + "/" + dd.getMonthValue() + "/" + dd.getYear());
            event_holder.getChildren().add(p);
        }
    }

    public void setHistories(Map<String, InventoryItemHistory> history){
        for(InventoryItemHistory h : history.values()) {
            histories.putIfAbsent(h.getInventoryID(), new ArrayList<InventoryItemHistory>());
            histories.get(h.getInventoryID()).add(h);
        }
    }

    public void setMaintainenceItem(Map<String, InventoryMaintainenceClass> maintainenceMap) {
        this.maintainenceMap = maintainenceMap;
    }

    public void next_year(MouseEvent mouseEvent) {
        yearbox.setText(String.valueOf(Integer.parseInt(yearbox.getText())+1));
        setEvents();
    }

    public void prev_year(MouseEvent mouseEvent) {
        yearbox.setText(String.valueOf(Integer.parseInt(yearbox.getText())-1));
        setEvents();
    }
}
