package com.nimblefix.core;

import java.io.Serializable;

public class MaintainenceAssignedData implements Serializable {

    public static final String dateTimePattern = "yyyy-MM-dd'T'HH:mm:ssX";
    String oui,assignedTo,assignedBy,assignedDateTime,adminComments,completionDateTime;
    CompactInventoryItem inventoryItem;
    String floorID;

    public MaintainenceAssignedData(MaintainenceAssignedData data){
        this.oui = data.getOui();
        this.inventoryItem = data.getInventory();
    }

    private CompactInventoryItem getInventory() {
        return inventoryItem;
    }

    public void setInventoryItem(CompactInventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public MaintainenceAssignedData() { }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getAssignedDateTime() {
        return assignedDateTime;
    }

    public void setAssignedDateTime(String assignedDateTime) {
        this.assignedDateTime = assignedDateTime;
    }

    public void setAdminComments(String s) {
        this.adminComments = s;
    }

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

    public String getAdminComments() {
        return adminComments;
    }

    public String getCompletionDateTime() {
        return completionDateTime;
    }

    public void setCompletionDateTime(String completionDateTime) {
        this.completionDateTime = completionDateTime;
    }

    public CompactInventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public String getFloorID() {
        return floorID;
    }

    public void setFloorID(String floorID) {
        this.floorID = floorID;
    }
}
