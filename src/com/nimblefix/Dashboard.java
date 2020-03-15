package com.nimblefix;

import com.nimblefix.core.Category;
import com.nimblefix.core.InventoryItem;
import com.nimblefix.core.Organization;
import com.nimblefix.core.OrganizationalFloors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Dashboard implements Initializable {
    public Stage curr_stg;
    Organization org;

    OrganizationalFloors current_selected_floor=null;
    Image floor_background_image = null;
    InventoryItem current_selected_inventory = null;
    HashMap<String,String> categoryToColourMap;

    boolean adding_mode = false;

    @FXML CheckBox auto_exp_check;
    @FXML Canvas canvas;
    @FXML ListView floor_list;
    @FXML ScrollPane canvas_container;
    @FXML Label current_floor_string,about_id;
    @FXML TitledPane about_inventory_pane;
    @FXML TextField about_title;
    @FXML TextArea about_desc;
    @FXML ImageView about_qr;
    @FXML Button place_inventory_button, delete_inventory_button,about_save,category_mutate_button;
    @FXML ChoiceBox about_category_dropdown;

    public void add_Map(ActionEvent actionEvent) throws Exception{
        if(current_selected_floor==null) {
            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
            a.setHeaderText("Please select a floor.");
            a.setTitle("Error");
            a.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.jpg","*.jpeg","*.png","*.bmp"));

        File f = fileChooser.showOpenDialog(curr_stg);
        BufferedImage i = ImageIO.read(f);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(i,"png",bos);
        current_selected_floor.setBackground_map(bos.toByteArray());
        set_current_floor(floor_list.getSelectionModel().getSelectedItem().toString());
    }

    public void add_inventory(MouseEvent mouseEvent) {
        if(current_selected_floor==null) {
            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
            a.setHeaderText("Please select a floor.");
            a.setTitle("Error");
            a.showAndWait();
            return;
        }
        if(!adding_mode) {
            delete_inventory_button.setDisable(true);
            place_inventory_button.setText("Exit");
            adding_mode = true;
            canvas.setCursor(Cursor.CROSSHAIR);
        }
        else if(adding_mode){
            delete_inventory_button.setDisable(false);
            place_inventory_button.setText("Place Inventory");
            adding_mode=false;
            canvas.setCursor(Cursor.DEFAULT);
        }
    }

    public void place_inventory(MouseEvent mouseEvent) {
        if(adding_mode){
            InventoryItem i = new InventoryItem(null,org.generateUniqueInventoryID(),"Untitled Item","No description",mouseEvent.getX(),mouseEvent.getY());
            current_selected_floor.addInventoryItem(i);
            selectcurrentInventory(i);
            if(auto_exp_check.isSelected())
                about_inventory_pane.setExpanded(true);
            redraw();
        }
    }

    public void mouse_down(MouseEvent mouseEvent) {
        if(current_selected_floor==null)return;
        if(!adding_mode){
            InventoryItem i = current_selected_floor.getInventoryItem(new InventoryItem.Location(mouseEvent.getX(),mouseEvent.getY()));
            if(i==null){selectcurrentInventory(null); return;}
            selectcurrentInventory(i);
            if(auto_exp_check.isSelected())
                about_inventory_pane.setExpanded(true);
        }
    }

    public void node_movement(MouseEvent mouseEvent) {
        if(current_selected_inventory==null)return;
        current_selected_inventory.setLocation(new InventoryItem.Location(mouseEvent.getX(),mouseEvent.getY()));
        redraw();
    }

    private void selectcurrentInventory(InventoryItem i){
        current_selected_inventory = i;
        loadintoAboutInventory(i);
        redraw();
    }

    private void loadintoAboutInventory(InventoryItem i){
        if(i==null){about_inventory_pane.setExpanded(false); return;}
        about_id.setText("Unique ID : "+i.getId());
        about_title.setText(i.getTitle());
        about_desc.setText(i.getDescription());
        if(i.getCategoryTag()==null)
            about_category_dropdown.getSelectionModel().select(0);
        else
            about_category_dropdown.getSelectionModel().select(org.getCategoryfromCategoryID(i.getCategoryTag()).getCategoryString());
    }

    private void load_categories(){
        about_category_dropdown.getItems().clear();
        about_category_dropdown.getItems().add("No Category");
        for(Category c : org.getCategories())
            about_category_dropdown.getItems().add(c.getCategoryString());

        if(current_selected_inventory!=null) {
            Category t = org.getCategoryfromCategoryID(current_selected_inventory.getCategoryTag());
            if(t==null)
                about_category_dropdown.getSelectionModel().select(0);
            else
                about_category_dropdown.getSelectionModel().select(t);
        }

    }

    public void manage_category(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CategoryManagerUI.fxml"));
        Parent root = loader.load();

        Stage newStage = new Stage();

        ((CategoryManager)loader.getController()).curr_stg=newStage;
        ((CategoryManager)loader.getController()).setOrganization(org);

        newStage.setTitle("Category Manager");
        newStage.setScene(new Scene(root, 700, 500));
        newStage.setResizable(false);
        newStage.initModality(Modality.APPLICATION_MODAL);

        newStage.showAndWait();
        load_categories();
        generateCategoryIDtoColourMap();
        redraw();
    }

    private void generateCategoryIDtoColourMap(){
        categoryToColourMap.clear();
        for(Category c : org.getCategories())
            categoryToColourMap.put(c.getUniqueID(),c.getRepresentationColor());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        category_mutate_button.setText("\uD83E\uDC73");

        org = new Organization("Untitled Organization");

        categoryToColourMap = new HashMap<String, String>();

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

        about_inventory_pane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    if(current_selected_inventory==null){about_inventory_pane.setExpanded(false);return;}
                    about_inventory_pane.setPrefHeight(253);
                }
                else if(!newValue)
                    about_inventory_pane.setPrefHeight(27);
            }
        });

        about_title.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                current_selected_inventory.setTitle(newValue);
            }
        });

        about_desc.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                current_selected_inventory.setDescription(newValue);
            }
        });

        about_category_dropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue==null||current_selected_inventory==null)return;
                if(newValue.equals("No Category"))
                    current_selected_inventory.setCategoryTag(null);
                else {
                    Category category = org.getCategoryfromCategoryString(newValue.toString());
                    if(category==null){
                        current_selected_inventory.setCategoryTag(null);
                        return;
                    }
                    current_selected_inventory.setCategoryTag(category.getUniqueID());
                }
            }
        });

        canvas.getGraphicsContext2D().fillText("Select a floor to view.",canvas.getWidth()/2,canvas.getHeight()/2);
        load_categories();
    }

    public void mutate_clicked(MouseEvent mouseEvent) {
        Category category = org.getCategoryfromCategoryID(current_selected_inventory.getCategoryTag());
        if(category==null)return;
        about_title.setText(category.getDefaultTitle());
        about_desc.setText(category.getDefaultDescription());
    }

    private void set_current_floor(String floor){
        current_floor_string.setText(floor);
        current_selected_floor = org.getFloor(floor);

        floor_background_image = null;
        if(current_selected_floor.getBackground_map()!=null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(current_selected_floor.getBackground_map());
            try {
                floor_background_image = SwingFXUtils.toFXImage(ImageIO.read(bis), null);
            }catch(Exception e){}
        }

        redraw();
    }

    public void add_Floor(MouseEvent mouseEvent) {
        String floor_str = "UnititledFloor";
        int i = 1;
        while(org.floorExists(floor_str+i))
            i++;
        org.addFloor(new OrganizationalFloors(floor_str+i));
        refresh_floor_list();
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

        if(floor_background_image!=null) {
            canvas.setHeight(floor_background_image.getHeight());
            canvas.setWidth(floor_background_image.getWidth());
            painter.drawImage(floor_background_image, 0, 0);
        }

        if(current_selected_floor!=null) {
            for (InventoryItem i : current_selected_floor.getInventories()) {
                if (i.equals(current_selected_inventory)) {
                    painter.setFill(Color.valueOf("#000000"));
                    painter.fillOval(i.getLocation().getX() - 5, i.getLocation().getY() - 5, 10, 10);
                    painter.setLineWidth(1);
                    painter.strokeOval(i.getLocation().getX() - 7, i.getLocation().getY() - 7, 14, 14);
                    painter.strokeOval(i.getLocation().getX() - 9, i.getLocation().getY() - 9, 18, 18);
                    painter.strokeOval(i.getLocation().getX() - 11, i.getLocation().getY() - 11, 22, 22);
                } else {
                    String colorHex = categoryToColourMap.get(i.getCategoryTag());
                    if (colorHex == null) colorHex = "#314cb6";
                    painter.setFill(Color.valueOf(colorHex));
                    painter.fillOval(i.getLocation().getX() - 5, i.getLocation().getY() - 5, 10, 10);
                }
            }
        }
    }

}


