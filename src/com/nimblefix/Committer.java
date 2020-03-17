package com.nimblefix;

import com.nimblefix.core.Category;
import com.nimblefix.core.Organization;
import com.nimblefix.core.OrganizationalFloors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Committer implements Initializable {
    public Stage curr_stg;
    @FXML Label label1,name_box,oui_box,floor_c,cat_c,inv_c;
    @FXML Pane commit_button;

    Organization selectedOrganization;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label1.setText("Commit     \u2794");
        commit_button.setOnMouseEntered(event -> commit_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#236592"), CornerRadii.EMPTY, Insets.EMPTY))));

        commit_button.setOnMouseExited(event -> commit_button.setBackground(new Background(new BackgroundFill(Color.valueOf("#3282b8"), CornerRadii.EMPTY, Insets.EMPTY))));
    }

    public void getandProceed(Organization selectedOrganization){
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
}
