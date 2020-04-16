package com.nimblefix;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import sun.font.FontFamily;

import javax.swing.plaf.synth.ColorType;
import java.net.URL;
import java.util.ResourceBundle;

public class InventoryHistory implements Initializable {
    public Stage curr_stg;

    @FXML Canvas calendar;
    @FXML Canvas month_header;
    @FXML Canvas date_header;

    @FXML AnchorPane monthPane;
    @FXML AnchorPane datePane;
    @FXML ScrollPane mainViewPoint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()->{
            initCalendar();
        });


    }

    private void initCalendar() {
        mainViewPoint.vvalueProperty().addListener((observable, oldValue, newValue) -> monthPane.setTranslateY(newValue.doubleValue()*(calendar.getHeight()-mainViewPoint.getViewportBounds().getHeight()+50)));
        mainViewPoint.hvalueProperty().addListener((observable, oldValue, newValue) -> datePane.setTranslateX(newValue.doubleValue()*(calendar.getWidth()-mainViewPoint.getViewportBounds().getWidth()+50)));

        mainViewPoint.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            monthPane.setTranslateY( mainViewPoint.getVvalue()*(calendar.getHeight()-newValue.getHeight()+50));
            datePane.setTranslateX( mainViewPoint.getHvalue()*(calendar.getWidth()-newValue.getWidth()+50));

        });
        calendar.setTranslateY(50);
        calendar.setTranslateX(50);
        drawCalendar();
    }

    private void drawCalendar() {
        double xDIV = calendar.getWidth()/12;
        double yDIV = calendar.getHeight()/31;

        GraphicsContext context = calendar.getGraphicsContext2D();
        GraphicsContext month_canvas_context = month_header.getGraphicsContext2D();
        GraphicsContext date_canvas_context = date_header.getGraphicsContext2D();

        context.setFill(Color.valueOf("#ffffff"));
        month_canvas_context.setFill(Color.valueOf("#ffffff"));
        date_canvas_context.setFill(Color.valueOf("#ffffff"));

        context.fillRect(0,0,calendar.getWidth(),calendar.getHeight());
        month_canvas_context.fillRect(0,0,month_header.getWidth(),month_header.getHeight());
        date_canvas_context.fillRect(0,0,date_header.getWidth(),date_header.getHeight());

        context.setStroke(Color.valueOf("#000000"));
        context.setLineWidth(0.1);
        month_canvas_context.setLineWidth(1);
        date_canvas_context.setLineWidth(1);

        //Months
        for(int i = 01; i<= 12; i++) {
            context.strokeLine(i * xDIV, 0, i * xDIV, calendar.getHeight());
            month_canvas_context.strokeLine(i * xDIV, 0, i * xDIV, month_header.getHeight());
        }
        month_canvas_context.setFill(Color.valueOf("#000000"));
        String[] months = new String[]{"January","February","March","April","May","June","July","August","September", "October","November","December"};
        month_canvas_context.setFont(Font.font(null , FontWeight.BOLD,20));
        for(int i = 0; i< 12; i++)
            month_canvas_context.fillText(months[i],i*xDIV+15,35);


        //Days
        for(int i = 01; i<= 31; i++) {
            context.strokeLine(0, i * yDIV, calendar.getWidth(), i * yDIV);
            date_canvas_context.strokeLine(0, i * yDIV, date_header.getWidth(), i * yDIV);
        }

        date_canvas_context.setFill(Color.valueOf("#000000"));
        date_canvas_context.setFont(Font.font(null , FontWeight.BOLD,20));
        for(int i = 0; i< 31; i++)
            date_canvas_context.fillText(String.valueOf(i+1),10,yDIV*i + 60);
    }
}
