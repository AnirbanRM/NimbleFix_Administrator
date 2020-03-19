package com.nimblefix;

import com.nimblefix.ControlMessages.OrganizationsExchangerMessage;
import com.nimblefix.core.Category;
import com.nimblefix.core.Organization;
import com.nimblefix.core.OrganizationalFloors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Committer implements Initializable {
    public Stage curr_stg;
    @FXML Label label1,name_box,oui_box,floor_c,cat_c,inv_c;
    @FXML Pane commit_button;

    Client client;

    Organization selectedOrganization;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label1.setText("Commit     \u2794");
        commit_button.setOnMouseEntered(event -> commit_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#236592"), CornerRadii.EMPTY, Insets.EMPTY))));

        commit_button.setOnMouseExited(event -> commit_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#3282b8"), CornerRadii.EMPTY, Insets.EMPTY))));
    }

    public void getandProceed(Organization selectedOrganization,Client client){
        this.client=client;
        this.selectedOrganization=selectedOrganization;

        floor_c.setText(String.valueOf(selectedOrganization.getFloorscount()));
        cat_c.setText(String.valueOf(selectedOrganization.getCategories().size()));
        int j = 0;
        for(OrganizationalFloors f : selectedOrganization.getFloors())
            j+=f.inventoryCount();
        inv_c.setText(String.valueOf(j));

        if(selectedOrganization.getOui()==null){
            Calendar i = Calendar.getInstance();
            selectedOrganization.setOui(""+i.get(Calendar.DATE)+i.get(Calendar.MONTH)+i.get(Calendar.YEAR)+i.get(Calendar.HOUR)+i.get(Calendar.MINUTE)+i.get(Calendar.SECOND)+i.get(Calendar.MILLISECOND));
        }

        oui_box.setText(selectedOrganization.getOui());
        name_box.setText(selectedOrganization.getOrganization_Name());
    }

    public void setSelectedOrganization(Organization organization){
        this.selectedOrganization=organization;
    }

    public Organization getSelectedOrganization(){
        return this.selectedOrganization;
    }

    public void commit_clicked(MouseEvent mouseEvent) {
        OrganizationsExchangerMessage organizationsExchangerMessage = new OrganizationsExchangerMessage(client.clientID,OrganizationsExchangerMessage.messageType.CLIENT_POST);
        organizationsExchangerMessage.addOrganization(Committer.this.selectedOrganization);

        Thread writerthd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.WRITER.reset();
                    client.WRITER.writeUnshared(organizationsExchangerMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Object reply=null;
                try{
                    reply = client.readNext();
                    if(reply instanceof OrganizationsExchangerMessage && ((OrganizationsExchangerMessage) reply).getBody().equals("SUCCESSFULLY SAVED")){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert a = new Alert(Alert.AlertType.INFORMATION ,null, ButtonType.OK);
                                a.setHeaderText("Commit Successful");
                                a.setTitle("Success");
                                a.initOwner(client.getCurrentShowingStage());
                                curr_stg.close();
                                a.showAndWait();
                            }
                        });
                    }
                }catch (Exception e){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert a = new Alert(Alert.AlertType.ERROR ,null, ButtonType.OK);
                            a.setHeaderText("Commit Unsuccessful");
                            a.setTitle("Failure");
                            a.showAndWait();
                        }
                    });
                    e.printStackTrace(); }
            }
        });
        writerthd.start();
    }
}
