package com.nimblefix.core;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Complaint implements Serializable {

    private String organizationID;
    private String inventoryID;

    private String userID;
    private String userRemarks;
    private String complaintDateTime;

    private String assignedBy;
    private String assignedDateTime;
    private String assignedTo;

    private String adminComments;

    private int problemStatus;
    private String dbID;

    public static final String dateTimePattern = "yyyy-MM-dd'T'HH:mm:ssX";

    public class Status{
        final public static int FIXED=1;
        final public static int UNFIXED=2;
        final public static int IGNORED=3;
    }

    public Complaint(String organizationID, String inventoryID, String userID, String userRemarks, String complaintDateTime){
        this.organizationID = organizationID;
        this.inventoryID = inventoryID;
        this.userID = userID;
        this.userRemarks = userRemarks;
        this.complaintDateTime = complaintDateTime;
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

    public String getOrganizationID() {
        return organizationID;
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserRemarks() {
        return userRemarks;
    }

    public String getComplaintDateTime() {
        return complaintDateTime;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public String getAssignedDateTime() {
        return assignedDateTime;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getAdminComments() {
        return adminComments;
    }

    public int getProblemStatus() {
        return problemStatus;
    }

    public String getDbID() {
        return dbID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserRemarks(String userRemarks) {
        this.userRemarks = userRemarks;
    }

    public void setComplaintDateTime(String complaintDateTime) {
        this.complaintDateTime = complaintDateTime;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public void setAssignedDate(String assignedDate) {
        this.assignedDateTime = assignedDate;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }

    public void setProblemStatus(int problemStatus) {
        this.problemStatus = problemStatus;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    public String getComplaintID(){
        return "COMP"+  String.format("%010d", Long.parseLong(dbID));

    }

}