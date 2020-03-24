package com.nimblefix.core;

import java.io.Serializable;
import java.util.Date;

public class Complaint implements Serializable {

    private String complaintID;

    private String organizationID;
    private String inventoryID;

    private String userID;
    private String userRemarks;
    private Date complaintDateTime;

    private String assignedBy;
    private String assignedDate;
    private String assignedTo;
    private String adminComments;
    private int status;

    private String problemStatus;
    private String dbID;

    public class Status{
        final public static int FIXED=1;
        final public static int UNFIXED=2;
        final public static int IGNORED=3;
    }

    public String getComplaintID() {
        return complaintID;
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

    public Date getComplaintDateTime() {
        return complaintDateTime;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public String getAssignedDate() {
        return assignedDate;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getAdminComments() {
        return adminComments;
    }

    public int getStatus() {
        return status;
    }

    public String getProblemStatus() {
        return problemStatus;
    }

    public String getDbID() {
        return dbID;
    }

    public void setComplaintID(String complaintID) {
        this.complaintID = complaintID;
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

    public void setComplaintDateTime(Date complaintDateTime) {
        this.complaintDateTime = complaintDateTime;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public void setAssignedDate(String assignedDate) {
        this.assignedDate = assignedDate;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setProblemStatus(String problemStatus) {
        this.problemStatus = problemStatus;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }
}