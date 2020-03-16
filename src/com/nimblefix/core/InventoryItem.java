package com.nimblefix.core;


import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class InventoryItem implements Serializable {

    public class HistoryItem implements Serializable{
        Date datetimestamp;
        String remarks;
        String result;

        HistoryItem(Date date, String remarks, String result){
            this.datetimestamp=date;
            this.remarks=remarks;
            this.result=result;
        }

        public Date getDatetimestamp() {
            return datetimestamp;
        }

        public String getRemarks() {
            return remarks;
        }

        public String getResult() {
            return result;
        }
    }


    public static class Location implements Serializable{
        double X,Y;
        public Location(double X, double Y){
            this.X=X;
            this.Y=Y;
        }

        public double getX() {
            return X;
        }

        public double getY() {
            return Y;
        }

        public void setX(double x) {
            X = x;
        }

        public void setY(double y) {
            Y = y;
        }
    }

    Organization parentOrganization;
    String id;
    String categoryTag;
    String title;
    String description;
    Location location;
    ArrayList<HistoryItem> History;

    public InventoryItem(Organization organization, String ID, String title, String description, double X, double Y){
        this.parentOrganization = organization;
        this.id=ID;
        this.title = title;
        this.description=description;
        location = new Location(X,Y);
        this.History = new ArrayList<HistoryItem>();
        this.categoryTag = null;
    }

    public String getOui() {
        return parentOrganization.oui;
    }

    public void setParentOrganization(Organization newOrganization){
        this.parentOrganization=newOrganization;
    }

    public Organization getParentOrganization(){
        return this.parentOrganization;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation(){
        return this.location;
    }

    public String getCategoryTag() {
        return categoryTag;
    }

    public void setCategoryTag(String categoryTag) {
        this.categoryTag = categoryTag;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setHistory(ArrayList<HistoryItem> history) {
        History = history;
    }

    public void clearHistory(){
        History.clear();
    }

    public void addtoHistory(Date date, String REMARKS, String RESULT){
        History.add(new HistoryItem(date,REMARKS,RESULT));
    }

    public ArrayList<HistoryItem> getHistory(){
        return this.History;
    }

    public Image getQRCode() {
        Canvas sticker = new Canvas(250, 250);
        GraphicsContext painter = sticker.getGraphicsContext2D();

        QRCodeWriter qrg = new QRCodeWriter();
        BitMatrix byteMatrix = null;
        try {
            byteMatrix = qrg.encode(parentOrganization.oui + "/" + id, BarcodeFormat.QR_CODE, 250, 250);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        Image t = SwingFXUtils.toFXImage(MatrixToImageWriter.toBufferedImage(byteMatrix), null);
        painter.drawImage(t, 0, 0);

        painter.setFont(Font.font("monospaced", FontWeight.BOLD, 10));
        painter.fillText(parentOrganization.oui + " / " + id, 30, 20);
        painter.fillText("NimbleFix QR Codes", 30, 235);

        painter.setStroke(Color.valueOf("#000000"));
        painter.setLineWidth(2);
        painter.strokeRoundRect(10, 10, 230, 230, 30, 30);

        WritableImage temp_img = new WritableImage(250, 250);
        sticker.snapshot(new SnapshotParameters(), temp_img);

        return SwingFXUtils.toFXImage(SwingFXUtils.fromFXImage(temp_img, null), null);
    }
}
