package com.nimblefix;

import com.nimblefix.core.InventoryItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import sun.font.FontFamily;
import sun.util.calendar.LocalGregorianCalendar;

import javax.swing.plaf.synth.ColorType;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class InventoryHistory implements Initializable, EventHandler<MouseEvent> {
    public Stage curr_stg;

    @FXML Canvas calendar;
    @FXML Canvas month_header;
    @FXML Canvas date_header;
    @FXML Canvas overlay_canvas;

    @FXML AnchorPane monthPane;
    @FXML AnchorPane datePane;
    @FXML AnchorPane event_holder;

    @FXML ScrollPane mainViewPoint;

    GraphicsContext context,month_canvas_context,date_canvas_context;

    double xDIV,yDIV;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        addEvent(new InventoryHistory());
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


    ArrayList<InventoryHistory> histories = new ArrayList<InventoryHistory>();
    public void addEvent(InventoryHistory history){
        histories.add(history);
        Pane p = new Pane();
        p.setMinHeight(yDIV-10);
        p.setMinWidth(xDIV-20);
        p.setBackground(new Background(new BackgroundFill(Color.valueOf("#4D089A"),new CornerRadii(10),Insets.EMPTY)));
        ArrayList<Double> dashes = new ArrayList<>();dashes.add(new Double(2)); dashes.add(new Double(10));
        p.setBorder(new Border(new BorderStroke(null,null,null,Color.WHITE,null,null,null,new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.BEVEL , StrokeLineCap.SQUARE ,200,0, dashes) ,CornerRadii.EMPTY,new BorderWidths(0,0,0,5),Insets.EMPTY )));

        p.setLayoutX(2*xDIV-2);
        p.setLayoutY(3*yDIV+5);

        event_holder.getChildren().add(p);
    }

    //On overlay_canvas hovered
    @Override
    public void handle(MouseEvent event) {
        redrawGridLines();
        context.setFill(Color.valueOf("#ededed"));
        context.fillRect(Math.floor(event.getX()/xDIV)*xDIV,Math.floor(event.getY()/yDIV)*yDIV,xDIV,yDIV);
    }
}
