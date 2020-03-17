package com.nimblefix;

import com.nimblefix.ControlMessages.OrganizationsExchangerMessage;
import com.nimblefix.core.Organization;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Dashboard implements Initializable {
    @FXML Label icon1,icon2,connected_label,user_label,new_file_button,load_file_button;
    @FXML ImageView logo_box,logo3,logo4;
    @FXML Pane fabricate_button,spectate_button;
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

        fabricate_button.setOnMouseEntered(event -> fabricate_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"), CornerRadii.EMPTY, Insets.EMPTY))));

        spectate_button.setOnMouseEntered(event -> spectate_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"), CornerRadii.EMPTY, Insets.EMPTY))));

        fabricate_button.setOnMouseExited(event -> fabricate_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#320670"), CornerRadii.EMPTY, Insets.EMPTY))));

        spectate_button.setOnMouseExited(event -> spectate_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#320670"), CornerRadii.EMPTY, Insets.EMPTY))));

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
        ((Editor)loader.getController()).curr_stg=primaryStage;
        ((Editor)loader.getController()).client=client;

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
                    client.WRITER.writeObject(organizationsExchangerMessage);
                    Object obj = client.readNext();
                    addtoList((OrganizationsExchangerMessage)obj);
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

                    OrgIDLabel.setText(item.getOrganizationID());
                    OrgNameLabel.setText(item.organizationName);

                    p.getChildren().addAll(OrgNameLabel,OrgIDLabel);

                    setGraphic(p);
                }
                else{
                    setGraphic(null);
                }
            }
        });


        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(oldValue!=null)
                    ((ListItem)oldValue).getCell().setBackground(new Background(new BackgroundFill(Color.valueOf("#efefef"),CornerRadii.EMPTY,Insets.EMPTY)));
                ((ListItem)newValue).getCell().setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"),CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
    }

    public void fabricate_clicked(MouseEvent mouseEvent) throws IOException {
        if(list.getSelectionModel().getSelectedIndex()>=0){

            OrganizationsExchangerMessage organizationsExchangerMessage = new OrganizationsExchangerMessage(client.clientID,OrganizationsExchangerMessage.messageType.CLIENT_GET);
            organizationsExchangerMessage.setBody(((ListItem)list.getSelectionModel().getSelectedItem()).getOrganizationID());

            client.WRITER.writeObject(organizationsExchangerMessage);
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

    private void startFabricate(Organization organization) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditorUI.fxml"));
        Parent root = loader.load();
        Stage primaryStage= new Stage();
        primaryStage.setTitle("Organization Fabricator");
        primaryStage.setScene(new Scene(root, 1200, 700));

        ((Editor)loader.getController()).curr_stg=primaryStage;
        ((Editor)loader.getController()).client=client;
        ((Editor)loader.getController()).loadFromServer(organization);

        client.getCurrentShowingStage().hide();
        client.setCurrentShowingStage(primaryStage);

        primaryStage.show();
    }

    public void spectate_clicked(MouseEvent mouseEvent) {
    }


}


