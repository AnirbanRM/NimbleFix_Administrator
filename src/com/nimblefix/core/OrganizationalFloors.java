package com.nimblefix.core;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class OrganizationalFloors implements Serializable {

    String floorID;
    byte[] background_map=null;

    ConcurrentHashMap<String,InventoryItem> inventories = new ConcurrentHashMap<String,InventoryItem>();

    public OrganizationalFloors(String floorID){
        this.floorID = floorID;
    }

    public String getFloorID() {
        return floorID;
    }

    public void setBackground_map(byte[] background_map) {
        this.background_map = background_map;
    }

    public byte[] getBackground_map() {
        return background_map;
    }

    public void setFloorID(String floorID) {
        this.floorID=floorID;
    }

    public ConcurrentHashMap<String,InventoryItem> getInventories(){
        return this.inventories;
    }

    public InventoryItem getInventoryItem(InventoryItem.Location l){
        for(String item  : inventories.keySet()){
            if(Math.abs(inventories.get(item).location.X-l.X)<=5&&Math.abs(inventories.get(item).location.Y-l.Y)<=5)
                return inventories.get(item);
        }
        return null;
    }

    public void addInventoryItem(InventoryItem item){
        inventories.put(item.getId(),item);
    }

    public int inventoryCount(){
        return inventories.size();
    }


    public void removeInventory(InventoryItem inventoryItem){
        inventories.remove(inventoryItem.getId());
    }
}
