package com.nimblefix;

import com.nimblefix.core.Category;
import com.nimblefix.core.Organization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;

public class CategoryManager {

    @FXML Button add,delete;
    @FXML TableView table;
    @FXML TableColumn category_str_column,colour_column,title_column,desc_column;

    public Stage curr_stg;
    Organization organization;
    ArrayList<Category> categories;

    public void setOrganization(Organization organization){
        this.organization = organization;
        categories = new ArrayList<Category>();
        for(Category c : organization.getCategories())
            categories.add(c);

        category_str_column.setCellValueFactory(new PropertyValueFactory<Category,String>("categoryString"));
        category_str_column.setCellFactory(TextFieldTableCell.forTableColumn());
        category_str_column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                if(organization.categoryExist(event.getNewValue().toString()))return;
                categories.get(event.getTablePosition().getRow()).setCategoryString(event.getNewValue().toString());
                refresh_table();
            }
        });

        title_column.setCellValueFactory(new PropertyValueFactory<Category,String>("defaultTitle"));
        title_column.setCellFactory(TextFieldTableCell.forTableColumn());
        title_column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                categories.get(event.getTablePosition().getRow()).setDefaultTitle(event.getNewValue().toString());
                refresh_table();
            }
        });

        desc_column.setCellValueFactory(new PropertyValueFactory<Category,String>("defaultDescription"));
        desc_column.setCellFactory(TextFieldTableCell.forTableColumn());
        desc_column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                categories.get(event.getTablePosition().getRow()).setDefaultDescription(event.getNewValue().toString());
                refresh_table();
            }
        });

        colour_column.setCellValueFactory(new PropertyValueFactory<>("representationColor"));
        colour_column.setCellFactory(new Callback<TableColumn<Category, String>, TableCell<Category, String>>() {

            @Override
            public TableCell<Category, String> call(TableColumn<Category, String> param) {

                TableCell<Category, String> cell = new TableCell<Category, String>() {
                    ColorPicker cp = new ColorPicker();

                    @Override
                    public void updateItem(String colorHex, boolean empty) {
                        super.updateItem(colorHex, empty);
                        if (!empty) {

                            cp.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    categories.get(getIndex()).setRepresentationColor(cp.getValue().toString());
                                    refresh_table();
                                }
                            });

                            cp.setStyle("-fx-color-label-visible: false;");
                            cp.setPrefHeight(20);
                            cp.setValue(Color.valueOf(colorHex));
                            setGraphic(cp);
                        }
                        else{
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        });
        refresh_table();
    }

    public void add_clicked(MouseEvent mouseEvent) {
        String str = "UntitledCategory";
        int i = 1;
        while(categoryExist(str+i))
            i++;
        categories.add(new Category(str+i));
        refresh_table();
    }

    private boolean categoryExist(String categoryString){
        for (Category c : categories){
            if(categoryString.equals(c.getCategoryString()))
                return true;
        }
        return false;
    }

    public void delete_clicked(MouseEvent mouseEvent) {
        if(table.getSelectionModel().getSelectedIndex()>=0){
            categories.remove(table.getSelectionModel().getSelectedIndex());
            refresh_table();
        }
    }

    private void refresh_table(){
        table.getItems().clear();
        for(Category c : categories){
            table.getItems().add(c);
        }
    }


    public void apply_clicked(MouseEvent mouseEvent) {
        organization.setCategories(categories);
        curr_stg.close();
    }

    public void exit_clicked(MouseEvent mouseEvent) {
        curr_stg.close();
    }
}