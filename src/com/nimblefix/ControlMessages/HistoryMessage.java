package com.nimblefix.ControlMessages;

import com.nimblefix.core.InventoryItemHistory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HistoryMessage implements Serializable {

    String oui,body;
    Map<String, InventoryItemHistory> histories = new HashMap<>();

    public HistoryMessage(String organizationID) {
        this.oui = organizationID;
    }

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, InventoryItemHistory> getHistories() {
        return histories;
    }

    public InventoryItemHistory getInventoryHistory(String inventoryID){
        return histories.get(inventoryID);
    }

    public void addHistory(String inventoryID, InventoryItemHistory history){
        histories.put(inventoryID,history);
    }

    public void setHistories(Map<String, InventoryItemHistory> histories) {
        this.histories = histories;
    }
}