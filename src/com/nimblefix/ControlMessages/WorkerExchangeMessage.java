package com.nimblefix.ControlMessages;

import com.nimblefix.core.Worker;

import java.io.Serializable;
import java.util.ArrayList;

public class WorkerExchangeMessage implements Serializable {

    private ArrayList<Worker> workers = new ArrayList<>();
    private String body,organizationID,clientID;

    public WorkerExchangeMessage(String clientID, String organizationID, ArrayList<Worker> workers){
        this.workers = workers;
        this.organizationID = organizationID;
        this.clientID = clientID;
        body = "";
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(ArrayList<Worker> workers) {
        this.workers = workers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
}