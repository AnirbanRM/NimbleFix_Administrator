package com.nimblefix.ControlMessages;

import com.nimblefix.core.Organization;

import java.io.Serializable;

public class MonitorMessage implements Serializable {

    String organizationID;
    Organization organization;
    String adminID;
    String body;

    int messageType;

    public MonitorMessage(String clientID, String organizationID, int msgType){
        this.adminID = clientID;
        this.organizationID = organizationID;
        this.messageType =msgType;

    }

    public class MessageType{
        public final static int CLIENT_MONITOR_START = 0;
        public final static int CLIENT_MONITOR_STOP = 1;
        public final static int CLIENT_MONITOR_COMPLAINT = 2;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
