package com.nimblefix;

import com.nimblefix.core.InventoryItem;
import com.nimblefix.core.Organization;
import com.nimblefix.core.OrganizationalFloors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Dashboard implements Initializable {

    Organization org;
    OrganizationalFloors current_selected_floor=null;

    boolean adding_mode = false;

    @FXML Canvas canvas;
    @FXML ListView floor_list;
    @FXML ScrollPane canvas_container;
    @FXML Label current_floor_string;

    public void add_inventory(MouseEvent mouseEvent) {
        if(current_selected_floor==null) {
            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
            a.setHeaderText("Please select a floor.");
            a.setTitle("Error");
            a.showAndWait();
            return;
        }
        adding_mode=true;
        canvas.setCursor(Cursor.CROSSHAIR);
    }

    public void place_inventory(MouseEvent mouseEvent) {
        if(adding_mode){
            current_selected_floor.addInventoryItem(new InventoryItem(null,org.getUniqueID(),"Empty Description",mouseEvent.getX(),mouseEvent.getY()));
            redraw();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        org = new Organization("Untitled Organization");

        floor_list.setEditable(true);
        floor_list.setCellFactory(TextFieldListCell.forListView());

        floor_list.setOnEditCommit(new EventHandler<ListView.EditEvent>() {
            @Override
            public void handle(ListView.EditEvent event) {
                int selected_index = floor_list.getSelectionModel().getSelectedIndex();
                if(org.floorExists(event.getNewValue().toString()))
                    return;
                org.getFloor(selected_index).setFloorID(event.getNewValue().toString());
                refresh_floor_list();
                floor_list.getSelectionModel().select(selected_index);
            }
        });

        floor_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue==null)return;
                set_current_floor(newValue.toString());
            }
        });

        System.out.println(canvas_container.getWidth());
        canvas.getGraphicsContext2D().fillText("Select a floor to view.",canvas.getWidth()/2,canvas.getHeight()/2);
    }

    private void set_current_floor(String floor){
        current_floor_string.setText(floor);
        current_selected_floor = org.getFloor(floor);
        redraw();
    }

    public void add_Floor(MouseEvent mouseEvent) {
        String floor_str = "UnititledFloor";
        int i = 1;
        while(org.floorExists(floor_str+i))
            i++;
        org.addFloor(new OrganizationalFloors(floor_str+i));
        refresh_floor_list();
        System.out.println(canvas_container.getWidth());
    }

    private void refresh_floor_list(){
        floor_list.getItems().clear();
        for(OrganizationalFloors f : org.getFloors()){
            floor_list.getItems().add(f.getFloorID());
        }
    }

    private void redraw(){
        GraphicsContext painter = canvas.getGraphicsContext2D();
        painter.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        painter.setFill(Color.valueOf("#4f009e"));
        for(InventoryItem i : current_selected_floor.getInventories())
            painter.fillOval(i.getLocation().getX()-10,i.getLocation().getY()-10,20,20);
    }

}
