package com.nimblefix;

import com.nimblefix.ControlMessages.HistoryMessage;
import com.nimblefix.ControlMessages.MaintainenceMessage;
import com.nimblefix.ControlMessages.MonitorMessage;
import com.nimblefix.ControlMessages.OrganizationsExchangerMessage;
import com.nimblefix.core.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Dashboard implements Initializable {
    @FXML Label icon1,icon2,connected_label,user_label,new_file_button,load_file_button;
    @FXML ImageView logo_box,logo3,logo4;
    @FXML Pane fabricate_button,spectate_button,workerMgmt,inventoryHistory,inventoryMaintainence;
    @FXML ListView list;

    public Stage curr_stg;
    Client client;

    private class ListItem{
        String organizationName;
        String organizationID;
        ListCell<ListItem> cell;

        ListItem(String organizationName,String organizationID){
            this.organizationID = organizationID;
            this.organizationName = organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        public void setOrganizationID(String organizationID) {
            this.organizationID = organizationID;
        }

        public void setCell(ListCell<ListItem> cell) {
            this.cell = cell;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public String getOrganizationID() {
            return organizationID;
        }

        public ListCell<ListItem> getCell() {
            return cell;
        }

    }

    public void setAddressandUserandClient(String address,String user,Client client){
        this.client = client;
        user_label.setText(user);
        connected_label.setText(address);
        fetchOrganizations();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        icon1.setText("\uD83D\uDD8A");
        icon2.setText("\uD83D\uDC41");
        logo_box.setImage(new Image("file://" + getClass().getResource("/resources/nimblefix_logo.png").getPath(), 200, 150, true, true));
        logo3.setImage(new Image("file://" + getClass().getResource("/resources/server.png").getPath(), 20, 20, true, true));
        logo4.setImage(new Image("file://" + getClass().getResource("/resources/user.png").getPath(), 20, 20, true, true));

        Pane[] graphicButtons = new Pane[]{fabricate_button,workerMgmt,spectate_button,inventoryHistory,inventoryMaintainence};
        for(Pane p : graphicButtons){
            p.setOnMouseEntered(event -> p.setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"), CornerRadii.EMPTY, Insets.EMPTY))));
            p.setOnMouseExited(event -> p.setBackground(new Background(new BackgroundFill(Color.valueOf("#320670"), CornerRadii.EMPTY, Insets.EMPTY))));
        }

        new_file_button.setOnMouseEntered(event -> {
            new_file_button.setTextFill(Color.valueOf("#0C7B93"));
            new_file_button.setUnderline(true);
        });

        new_file_button.setOnMouseExited(event -> {
            new_file_button.setTextFill(Color.valueOf("#000000"));
            new_file_button.setUnderline(false);
        });

        load_file_button.setOnMouseEntered(event -> {
            load_file_button.setTextFill(Color.valueOf("#0C7B93"));
            load_file_button.setUnderline(true);

        });

        load_file_button.setOnMouseExited(event -> {
            load_file_button.setTextFill(Color.valueOf("#000000"));
            load_file_button.setUnderline(false);
        });

        prepareListView();
    }

    public void create_file(MouseEvent mouseEvent) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditorUI.fxml"));
        Parent root = loader.load();
        Stage primaryStage= new Stage();
        primaryStage.setTitle("Organization Fabricator");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(1200);
        ((Editor)loader.getController()).curr_stg=primaryStage;
        ((Editor)loader.getController()).client=client;

        client.getCurrentShowingStage().hide();
        client.setCurrentShowingStage(primaryStage);

        primaryStage.show();
    }

    public void load_file_clicked(MouseEvent mouseEvent) throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter efil = new FileChooser.ExtensionFilter("NimbleFix Maps","*.nfxm");
        fileChooser.getExtensionFilters().add(efil);
        File f = fileChooser.showOpenDialog(curr_stg);
        if(f==null)return;

        FileInputStream fi = new FileInputStream(f);
        ObjectInputStream oi = new ObjectInputStream(fi);
        Organization org = (Organization) oi.readObject();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditorUI.fxml"));
        Parent root = loader.load();
        Stage primaryStage= new Stage();
        primaryStage.setTitle("Organization Fabricator");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(1200);
        ((Editor)loader.getController()).curr_stg=primaryStage;

        ((Editor)loader.getController()).client=client;
        ((Editor)loader.getController()).loadFromOutside(org);

        client.getCurrentShowingStage().hide();
        client.setCurrentShowingStage(primaryStage);

        primaryStage.show();

    }

    public void fetchOrganizations() {
        Thread organizationFetcher = new Thread(new Runnable() {
            @Override
            public void run() {
                OrganizationsExchangerMessage organizationsExchangerMessage = new OrganizationsExchangerMessage(user_label.getText(),OrganizationsExchangerMessage.messageType.CLIENT_QUERY);
                try {
                    client.WRITER.writeUnshared(organizationsExchangerMessage);
                    Object obj = client.readNext();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addtoList((OrganizationsExchangerMessage)obj);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        organizationFetcher.start();
    }

    private void addtoList(OrganizationsExchangerMessage organizationsExchangerMessage){
        for(Organization o : organizationsExchangerMessage.getOrganizations())
            list.getItems().add(new ListItem(o.getOrganization_Name(),o.getOui()));
    }

    private void prepareListView() {
        list.setCellFactory(param -> new ListCell<ListItem>() {
            @Override
            protected void updateItem(ListItem item, boolean empty) {
                super.updateItem(item, empty);
                setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                if(!empty) {
                    item.setCell(this);
                    setHeight(60);

                    setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setBorder(new Border(new BorderStroke(null,null ,Color.valueOf("#bcbcbc"),null,BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,new CornerRadii(0),BorderWidths.DEFAULT,Insets.EMPTY)));

                    Pane p = new Pane();
                    p.setPrefHeight(60);

                    Label OrgIDLabel = new Label();
                    OrgIDLabel.setLayoutX(15);
                    OrgIDLabel.setLayoutY(35);
                    OrgIDLabel.setFont(Font.font(null, FontWeight.BOLD,12));

                    Label OrgNameLabel = new Label();
                    OrgNameLabel.setFont(new Font(18));
                    OrgNameLabel.setFont(Font.font(null, FontWeight.BOLD,18));
                    OrgNameLabel.setLayoutY(5);
                    OrgNameLabel.setLayoutX(5);

                    Label l = new Label();
                    l.setText("\uD83D\uDDD9");
                    l.setFont(Font.font(15));
                    l.setLayoutX(3);
                    l.setLayoutY(-1);
                    Pane delete_button = new Pane();
                    delete_button.setPrefHeight(21);
                    delete_button.setPrefWidth(21);
                    delete_button.getChildren().add(l);
                    delete_button.setLayoutX(280);
                    delete_button.setLayoutY(18);

                    delete_button.setOnMouseClicked(event -> {
                        deleteOrganization(item.getOrganizationID());

                    });

                    l.setOnMouseEntered(event -> {
                        if(isSelected()) {
                            l.setTextFill(Color.valueOf("#000000"));
                            delete_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffffff"), new CornerRadii(20) , Insets.EMPTY)));
                        }
                        else {
                            l.setTextFill(Color.valueOf("#ffffff"));
                            delete_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"), new CornerRadii(20), Insets.EMPTY)));
                        }
                    });

                    l.setOnMouseExited(event -> {
                        if(isSelected()) {
                            l.setTextFill(Color.valueOf("#ffffff"));
                            delete_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"), new CornerRadii(20), Insets.EMPTY)));
                        }
                        else {
                            l.setTextFill(Color.valueOf("#000000"));
                            delete_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), new CornerRadii(20), Insets.EMPTY)));
                        }
                    });

                    OrgIDLabel.setText(item.getOrganizationID());
                    OrgNameLabel.setText(item.organizationName);

                    p.getChildren().addAll(OrgNameLabel,OrgIDLabel,delete_button);

                    setGraphic(p);
                }
                else{
                    setGraphic(null);
                    setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setBorder(new Border(new BorderStroke(null,null,null,null)));
                }
            }
        });


        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            private Pane getButton(Object x){
                return (Pane)(((Pane) ((ListItem) x).getCell().getChildrenUnmodifiable().get(0)).getChildrenUnmodifiable().get(2));
            }

            private Label getLabel(Object x){
                return (Label) (((Pane)(((Pane) ((ListItem) x).getCell().getChildrenUnmodifiable().get(0)).getChildrenUnmodifiable().get(2))).getChildrenUnmodifiable().get(0));
            }

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue==null)return;

                Pane button = (Pane)(((Pane) ((ListItem) newValue).getCell().getChildrenUnmodifiable().get(0)).getChildrenUnmodifiable().get(2));
                Label button_txt = (Label) button.getChildrenUnmodifiable().get(0);

                if(oldValue!=null) {
                    ((ListItem) oldValue).getCell().setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), CornerRadii.EMPTY, Insets.EMPTY)));
                    getButton(oldValue).setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"), new CornerRadii(20) , Insets.EMPTY)));
                    getLabel(oldValue).setTextFill(Color.valueOf("#000000"));
                }
                ((ListItem)newValue).getCell().setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"),CornerRadii.EMPTY,Insets.EMPTY)));
                getButton(newValue).setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"), new CornerRadii(20) , Insets.EMPTY)));
                getLabel(newValue).setTextFill(Color.valueOf("#ffffff"));
            }
        });
    }

    private void deleteOrganization(String organizationID) {
        OrganizationsExchangerMessage organizationsExchangerMessage = new OrganizationsExchangerMessage(client.clientID,OrganizationsExchangerMessage.messageType.CLIENT_DELETE);
        organizationsExchangerMessage.setOrganizationOwner(client.clientID);
        organizationsExchangerMessage.setBody(organizationID);

        Thread del_thd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.WRITER.writeUnshared(organizationsExchangerMessage);
                    Object deleteReply = client.readNext();
                    if(deleteReply instanceof OrganizationsExchangerMessage && ((OrganizationsExchangerMessage) deleteReply).getBody().equals("SUCCESS")){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                list.getItems().clear();
                                prepareListView();
                                fetchOrganizations();
                            }
                        });
                    }
                } catch (IOException e) { }
            }
        });
        del_thd.start();
    }

    public void fabricate_clicked(MouseEvent mouseEvent) throws IOException {
        if(list.getSelectionModel().getSelectedIndex()>=0){

            OrganizationsExchangerMessage organizationsExchangerMessage = new OrganizationsExchangerMessage(client.clientID,OrganizationsExchangerMessage.messageType.CLIENT_GET);
            organizationsExchangerMessage.setBody(((ListItem)list.getSelectionModel().getSelectedItem()).getOrganizationID());

            client.WRITER.writeUnshared(organizationsExchangerMessage);
            Object organizationResponse = client.readNext();

            if(organizationResponse instanceof OrganizationsExchangerMessage){
                startFabricate(((OrganizationsExchangerMessage) organizationResponse).getOrganizations().get(0));
            }
        }
        else{
            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
            a.setHeaderText("Please select an Organization to fabricate.");
            a.setTitle("Error");
            a.showAndWait();
        }
    }

    public void workerMgmtClicked(MouseEvent mouseEvent) throws Exception{

        if(list.getSelectionModel().getSelectedIndex()>=0){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkerManagementUI.fxml"));
            Parent root = loader.load();
            Stage primaryStage= new Stage();
            primaryStage.setTitle("Worker Management");
            primaryStage.setScene(new Scene(root, 1100, 680));
            primaryStage.setMinHeight(680);
            primaryStage.setMinWidth(1100);

            ((WorkerManagement)loader.getController()).curr_stg=primaryStage;
            ((WorkerManagement)loader.getController()).client=client;
            ((WorkerManagement)loader.getController()).organizationName = ((ListItem) list.getSelectionModel().getSelectedItem()).organizationName;
            ((WorkerManagement)loader.getController()).organizationID = ((ListItem) list.getSelectionModel().getSelectedItem()).organizationID;

            client.getCurrentShowingStage().hide();
            client.setCurrentShowingStage(primaryStage);

            primaryStage.show();
        }
        else{
            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
            a.setHeaderText("Please select an Organization to manage workers.");
            a.setTitle("Error");
            a.showAndWait();
        }
    }

    private void startFabricate(Organization organization) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditorUI.fxml"));
        Parent root = loader.load();
        Stage primaryStage= new Stage();
        primaryStage.setTitle("Organization Fabricator");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(1200);

        ((Editor)loader.getController()).curr_stg=primaryStage;
        ((Editor)loader.getController()).client=client;
        ((Editor)loader.getController()).loadFromOutside(organization);

        client.getCurrentShowingStage().hide();
        client.setCurrentShowingStage(primaryStage);

        primaryStage.show();
    }

    public void spectate_clicked(MouseEvent mouseEvent) throws IOException {
        if(list.getSelectionModel().getSelectedIndex()>=0){

            MonitorMessage monitorMessage = new MonitorMessage(client.clientID, ((ListItem) list.getSelectionModel().getSelectedItem()).getOrganizationID(),MonitorMessage.MessageType.CLIENT_MONITOR_START);
            client.WRITER.writeUnshared(monitorMessage);
            Object monitorResponse = client.readNext();

            if(monitorResponse instanceof MonitorMessage){
                try {
                    startSpectate((MonitorMessage) monitorResponse);
                }catch (Exception e){ }
            }
        }
        else{
            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
            a.setHeaderText("Please select an Organization to spectate.");
            a.setTitle("Error");
            a.showAndWait();
        }
    }

    private void startSpectate(MonitorMessage monitorMessage) throws Exception{
        if(monitorMessage.getOrganization()!=null) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SpectatorUI.fxml"));
            Parent root = loader.load();
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Organization Spectator");
            primaryStage.setScene(new Scene(root, 1200, 700));
            primaryStage.setMinHeight(700);
            primaryStage.setMinWidth(1200);

            ((Spectator) loader.getController()).curr_stg = primaryStage;
            ((Spectator) loader.getController()).client = client;
            ((Spectator) loader.getController()).load(monitorMessage.getOrganization());
            ((Spectator) loader.getController()).startComplaintListener();

            client.getCurrentShowingStage().hide();
            client.setCurrentShowingStage(primaryStage);

            primaryStage.show();
        }
    }

    public void invMaintainClicked(MouseEvent mouseEvent) throws Exception{
        if(list.getSelectionModel().getSelectedIndex()>=0){

            FXMLLoader loader = new FXMLLoader(getClass().getResource("InventoryMaintainenceUI.fxml"));
            Parent root = loader.load();
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Organization Spectator");
            primaryStage.setScene(new Scene(root, 860, 520));
            primaryStage.setResizable(false);

            ((InventoryMaintainence) loader.getController()).curr_stg = primaryStage;
            ((InventoryMaintainence) loader.getController()).client = client;
            ((InventoryMaintainence) loader.getController()).setExtra( ((ListItem) list.getSelectionModel().getSelectedItem()).getOrganizationID(), ((ListItem) list.getSelectionModel().getSelectedItem()).getOrganizationName());

            primaryStage.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OrganizationsExchangerMessage organizationsExchangerMessage = new OrganizationsExchangerMessage(client.clientID, OrganizationsExchangerMessage.messageType.CLIENT_GET);
                        organizationsExchangerMessage.setBody(((ListItem) list.getSelectionModel().getSelectedItem()).getOrganizationID());

                        MaintainenceMessage maintainenceMessage = new MaintainenceMessage(((ListItem)list.getSelectionModel().getSelectedItem()).organizationID ,new HashMap<String, InventoryMaintainenceClass>());
                        maintainenceMessage.setBody("FETCH");

                        client.WRITER.reset();
                        client.WRITER.writeUnshared(organizationsExchangerMessage);
                        Object organizationResponse = client.readNext();

                        client.WRITER.reset();
                        client.WRITER.writeUnshared(maintainenceMessage);
                        Object maintainenceResponse = client.readNext();

                        if (organizationResponse instanceof OrganizationsExchangerMessage && maintainenceMessage instanceof MaintainenceMessage) {
                            Platform.runLater(() -> {
                                ((InventoryMaintainence) loader.getController()).setInventoryItems(((OrganizationsExchangerMessage) organizationResponse).getOrganizations().get(0).getCategories(), ((OrganizationsExchangerMessage) organizationResponse).getOrganizations().get(0).getFloors(), ((MaintainenceMessage) maintainenceResponse).getMaintainenceMap());
                            });
                        }

                    }catch (Exception e){ System.out.println(e.toString()); }
                }
            }).start();
        }
        else{
            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
            a.setHeaderText("Please select an Organization to spectate.");
            a.setTitle("Error");
            a.showAndWait();
        }
    }

    public void invHistoryClicked(MouseEvent mouseEvent) throws Exception{
        if(list.getSelectionModel().getSelectedIndex()>=0){

            FXMLLoader loader = new FXMLLoader(getClass().getResource("InventoryHistoryUI.fxml"));
            Parent root = loader.load();
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Inventory History");
            primaryStage.setScene(new Scene(root, 1300, 700));

            ((InventoryHistory) loader.getController()).curr_stg = primaryStage;
            ((InventoryHistory) loader.getController()).client = client;
            ((InventoryHistory) loader.getController()).setExtra( ((ListItem) list.getSelectionModel().getSelectedItem()).getOrganizationID(), ((ListItem) list.getSelectionModel().getSelectedItem()).getOrganizationName());

            primaryStage.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Getting Inventory Items
                        OrganizationsExchangerMessage organizationsExchangerMessage = new OrganizationsExchangerMessage(client.clientID, OrganizationsExchangerMessage.messageType.CLIENT_GET);
                        organizationsExchangerMessage.setBody(((ListItem) list.getSelectionModel().getSelectedItem()).getOrganizationID());

                        client.WRITER.writeUnshared(organizationsExchangerMessage);
                        Object organizationResponse = client.readNext();

                        if (organizationResponse instanceof OrganizationsExchangerMessage) {
                            Platform.runLater(()->{ ((InventoryHistory) loader.getController()).setInventoryItems(((OrganizationsExchangerMessage)organizationResponse).getOrganizations().get(0).getCategories(), ((OrganizationsExchangerMessage)organizationResponse).getOrganizations().get(0).getFloors()); });
                        }

                        //Getting History
                        HistoryMessage message = new HistoryMessage(((ListItem) list.getSelectionModel().getSelectedItem()).getOrganizationID());
                        message.setBody("FETCH");

                        try {
                            client.WRITER.reset();
                            client.WRITER.writeUnshared(message);
                        }catch (Exception e){}

                        Object historyMessage = client.readNext();
                        if(historyMessage instanceof HistoryMessage){
                            final HistoryMessage respMessage = (HistoryMessage) historyMessage;
                            Platform.runLater(()->{ ((InventoryHistory) loader.getController()).setHistories(respMessage.getHistories()); });
                        }

                        //Getting Maintainence
                        MaintainenceMessage maintainenceMessage = new MaintainenceMessage(((ListItem)list.getSelectionModel().getSelectedItem()).organizationID ,new HashMap<String, InventoryMaintainenceClass>());
                        maintainenceMessage.setBody("FETCH");

                        try {
                            client.WRITER.reset();
                            client.WRITER.writeUnshared(maintainenceMessage);
                        }catch (Exception e){}

                        Object maintainenceResponse = client.readNext();
                        if(maintainenceResponse instanceof MaintainenceMessage){
                            final MaintainenceMessage respMessage = (MaintainenceMessage) maintainenceResponse;
                            Platform.runLater(()->{ ((InventoryHistory) loader.getController()).setMaintainenceItem(respMessage.getMaintainenceMap()); });
                        }
                    }catch (Exception e){ }
                }
            }).start();
        }

        else{
            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
            a.setHeaderText("Please select an Organization to spectate.");
            a.setTitle("Error");
            a.showAndWait();
        }


    }


}


