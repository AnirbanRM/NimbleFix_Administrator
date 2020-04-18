package com.nimblefix.core;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class InventoryItemHistory implements Serializable {

    public static final String dateTimePattern = "yyyy-MM-dd'T'HH:mm:ssX";

    public static class Type{
        public static final int PAST = 0;
        public static final int FUTURE = 1;
    }

    String oui,inventoryID,workDateTime,assignedTo,eventID,eventBody;
    int eventType;

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public String getWorkDateTime() {
        return workDateTime;
    }

    public void setWorkDateTime(String workDateTime) {
        this.workDateTime = workDateTime;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventBody() {
        return eventBody;
    }

    public void setEventBody(String eventBody) {
        this.eventBody = eventBody;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public static String getDTString(Date datetime){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(dateTimePattern);
        df.setTimeZone(tz);
        return df.format(datetime);
    }

    public static Date getDTDate(String dateTimeISO) {
        DateFormat df1 = new SimpleDateFormat(dateTimePattern);
        try {
            return df1.parse(dateTimeISO);
        } catch (ParseException e) {
            return null;
        }
    }
}
