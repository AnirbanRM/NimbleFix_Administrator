package com.nimblefix;

import com.nimblefix.ControlMessages.MaintainenceMessage;
import com.nimblefix.core.Category;
import com.nimblefix.core.InventoryItem;
import com.nimblefix.core.InventoryMaintainenceClass;
import com.nimblefix.core.OrganizationalFloors;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class CustomTreeNode<T extends String> extends TreeItem<String>{

    public static class Type{
        public static int ROOT = 0;
        public static int CATEGORY = 1;
        public static int ITEM = 2;
    }

    private int type;
    private String uID;

    CustomTreeNode(T value,int type){
        super(value);
        this.type = type;
    }
    CustomTreeNode(T value,int type,String uID){
        super(value);
        this.type = type;
        this.uID = uID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String toString(){
        return this.uID + " " + (this.type==0?"ROOT":(this.type==1?"CATEGORY":"ITEM")) + " " + this.getValue();
    }
}

public class InventoryMaintainence implements Initializable, ChangeListener{

    Client client;
    Stage curr_stg;
    ArrayList<Category> categories;
    ArrayList<OrganizationalFloors> floors;
    String organizationID;
    String organizationName;

    CustomTreeNode currentNode;

    Map<String, InventoryMaintainenceClass> maintainenceMap = new HashMap<String,InventoryMaintainenceClass>();

    @FXML TreeView inventoryTree;
    @FXML ChoiceBox floordropdown;
    @FXML TextField search_box,freq;
    @FXML ToggleGroup G1,G2;
    @FXML DatePicker pDate;
    @FXML RadioButton disable,weekly,monthly,quarterly,halfyearly,yearly,today,particulardate;
    @FXML Button set,cancel_b,commit_b;
    @FXML Label orgID,orgName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        floordropdown.getSelectionModel().selectedItemProperty().addListener(this);
        search_box.textProperty().addListener(this);
        inventoryTree.getSelectionModel().selectedItemProperty().addListener(this);
        set.setOnMouseClicked((mouseEvent)->updateMaintainence(currentNode));
    }

    //Using recursively..
    public void updateMaintainence(CustomTreeNode selectedItem) {
        if (selectedItem.getType() == CustomTreeNode.Type.ROOT || selectedItem.getType() == CustomTreeNode.Type.CATEGORY) {
            for (Object o : selectedItem.getChildren().toArray())
                updateMaintainence((CustomTreeNode) o);
        }
        else if (selectedItem.getType() == CustomTreeNode.Type.ITEM) {
            if(disable.isSelected()){
                InventoryMaintainenceClass objMaintainence = maintainenceMap.get(selectedItem.getuID());
                freq.setText("");
                if(G2.getSelectedToggle()!=null)G2.getSelectedToggle().setSelected(false);
                pDate.setValue(null);
                if(objMaintainence!=null)
                    maintainenceMap.remove(selectedItem.getuID());
            }
            else {
                InventoryMaintainenceClass objMaintainence = maintainenceMap.get(selectedItem.getuID());
                if(objMaintainence==null) {
                    objMaintainence = new InventoryMaintainenceClass(organizationID,selectedItem.getuID());
                    maintainenceMap.put(selectedItem.getuID(),objMaintainence);
                }

                if(weekly.isSelected())objMaintainence.setType(InventoryMaintainenceClass.Type.WEEKLY);
                else if(monthly.isSelected())objMaintainence.setType(InventoryMaintainenceClass.Type.MONTHLY);
                else if(quarterly.isSelected())objMaintainence.setType(InventoryMaintainenceClass.Type.QUARTERLY);
                else if(halfyearly.isSelected())objMaintainence.setType(InventoryMaintainenceClass.Type.HALF_YEARLY);
                else if(yearly.isSelected())objMaintainence.setType(InventoryMaintainenceClass.Type.YEARLY);

                try {
                    objMaintainence.setFrequency(Double.parseDouble(freq.getText()));
                }catch (NumberFormatException e){ objMaintainence.setFrequency(1); }
                if (today.isSelected()) {
                    System.out.println(new SimpleDateFormat(InventoryMaintainenceClass.DATEPATTERN).format(new Date()));
                    objMaintainence.setLastMaintainenceDate(new SimpleDateFormat(InventoryMaintainenceClass.DATEPATTERN).format(new Date()));
                }
                else if(particulardate.isSelected()) {
                    try {
                        objMaintainence.setLastMaintainenceDate(LocalDate.from(pDate.getValue()).format(DateTimeFormatter.ofPattern(InventoryMaintainenceClass.DATEPATTERN)));
                    } catch (Exception e) { }
                }
            }
        }
    }

    //ChangeListener for Floor selection dropdown
    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(newValue instanceof String) {
            if (newValue == null || search_box == null) return;
            setFilterInventory(search_box.getText(), floordropdown.getSelectionModel().getSelectedItem().toString());
        }
        else if(newValue instanceof CustomTreeNode){
            CustomTreeNode item = (CustomTreeNode) newValue;
            currentNode = item;
            setMaintainenceItem();
        }
    }

    private void setMaintainenceItem() {
        if(currentNode.getType() == CustomTreeNode.Type.ROOT || currentNode.getType() == CustomTreeNode.Type.CATEGORY){
            if(G1.getSelectedToggle()!=null) G1.getSelectedToggle().setSelected(false);
            if(G2.getSelectedToggle()!=null) G2.getSelectedToggle().setSelected(false);
            freq.setText("");
            pDate.setValue(null);
        }

        else if(currentNode.getType()== CustomTreeNode.Type.ITEM){
            InventoryMaintainenceClass itemMaintainence = maintainenceMap.get(currentNode.getuID());
            if(itemMaintainence==null){
                disable.setSelected(true);
                if(G2.getSelectedToggle()!=null) G2.getSelectedToggle().setSelected(false);
                freq.setText("");
                pDate.setValue(null);
            }
            else{
                RadioButton[] freqType = new RadioButton[]{disable,weekly,monthly,quarterly,halfyearly,yearly};
                freqType[itemMaintainence.getType()].setSelected(true);
                freq.setText(String.valueOf(itemMaintainence.getFrequency()));

                try {
                    particulardate.setSelected(true);
                    pDate.setValue(LocalDate.parse(itemMaintainence.getLastMaintainenceDate(),DateTimeFormatter.ofPattern(InventoryMaintainenceClass.DATEPATTERN)));
                }catch (Exception e){
                    //pDate.setValue(null);
                }
            }
        }
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

    public void setInventoryItems(ArrayList<Category> categories, ArrayList<OrganizationalFloors> floors, Map<String, InventoryMaintainenceClass> maintainenceMap) {
        this.categories = categories;
        this.floors = floors;
        this.maintainenceMap = maintainenceMap;
        setFloorItems(floors);
        setFilterInventory(search_box.getText(), floordropdown.getSelectionModel().getSelectedItem().toString());
    }

    private void setFloorItems(ArrayList<OrganizationalFloors> floors) {
        floordropdown.getItems().add("Any Floor");
        for(OrganizationalFloors f : floors)
            floordropdown.getItems().add(f.getFloorID());
        floordropdown.getSelectionModel().select(0);
    }

    public void commitMaintainenceData(MouseEvent mouseEvent){
        new Thread(() -> {
            MaintainenceMessage maintainenceMessage = new MaintainenceMessage(organizationID,maintainenceMap);
            maintainenceMessage.setBody("CONFIG");
            maintainenceMessage.setOui(organizationID);
            try{
                client.WRITER.reset();
                client.WRITER.writeUnshared(maintainenceMessage);
            }catch (Exception e){ }

            Object o = client.readNext();
            if(o instanceof MaintainenceMessage){
                MaintainenceMessage m = (MaintainenceMessage)o;
                Platform.runLater(()->{
                    if(m.getBody().equals("SUCCESS")){
                        Alert a = new Alert(Alert.AlertType.INFORMATION ,null, ButtonType.OK);
                        a.setHeaderText("Successfully Updated.");
                        a.setTitle("Success");
                        a.showAndWait();
                        curr_stg.close();
                    }
                    else{
                        Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
                        a.setHeaderText("Error updating. Please retry...");
                        a.setTitle("Error");
                        a.showAndWait();
                    }
                });
            }
        }).start();
    }

    public void cancel(MouseEvent mouseEvent){
        curr_stg.close();
    }

    public void setExtra(String organizationID, String organizationName) {
        this.organizationID = organizationID;
        this.organizationName = organizationName;
        orgID.setText(organizationID);
        orgName.setText(organizationName);
    }

}