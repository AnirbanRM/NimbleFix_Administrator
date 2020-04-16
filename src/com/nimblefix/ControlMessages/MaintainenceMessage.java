package com.nimblefix.ControlMessages;

import com.nimblefix.core.InventoryMaintainenceClass;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MaintainenceMessage implements Serializable {
    String body;
    String oui;
    Map<String, InventoryMaintainenceClass> maintainenceMap = new HashMap<String,InventoryMaintainenceClass>();

    public MaintainenceMessage(String oui, Map<String,InventoryMaintainenceClass> maintainenceMap){
        this.oui = oui;
        this.maintainenceMap = maintainenceMap;
    }

    public String getBody() {
        return body;
    }

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, InventoryMaintainenceClass> getMaintainenceMap() {
        return maintainenceMap;
    }

    public void setMaintainenceMap(Map<String, InventoryMaintainenceClass> maintainenceMap) {
        this.maintainenceMap = maintainenceMap;
    }
}
