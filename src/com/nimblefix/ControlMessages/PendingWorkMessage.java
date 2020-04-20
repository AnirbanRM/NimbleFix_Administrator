package com.nimblefix.ControlMessages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PendingWorkMessage implements Serializable {

    String organizationID;
    String body;

    Map<String,Integer> pendingTasks = new HashMap<String,Integer>();

    public PendingWorkMessage(String organizationID) {
        this.organizationID = organizationID;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, Integer> getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(Map<String, Integer> pendingTasks) {
        this.pendingTasks = pendingTasks;
    }
}
