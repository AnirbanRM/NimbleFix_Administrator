package com.nimblefix.ControlMessages;

import com.nimblefix.core.Complaint;
import com.nimblefix.core.InventoryItem;

import java.io.Serializable;
import java.util.ArrayList;

public class ComplaintMessage implements Serializable {

    ArrayList<Complaint> complaints = new ArrayList<Complaint>();
    String body;
    byte[] location_image;
    InventoryItem inventoryItem;
    String floorID;

    public String getFloorID() {
        return floorID;
    }

    public void setFloorID(String floorID) {
        this.floorID = floorID;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public ComplaintMessage(Complaint complaint){ complaints.add(complaint); }

    public ComplaintMessage(ArrayList<Complaint> complaints){
        this.complaints = complaints;
    }

    public Complaint getComplaint() {
        return complaints.get(0);
    }

    public void setComplaint(Complaint complaint) {
        this.complaints.set(0,complaint);
    }

    public ArrayList<Complaint> getComplaints(){
        return this.complaints;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getLocation_image() {
        return location_image;
    }

    public void setLocation_image(byte[] location_image) {
        this.location_image = location_image;
    }
}
