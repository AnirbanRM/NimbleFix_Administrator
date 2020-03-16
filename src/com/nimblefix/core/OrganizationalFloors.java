package com.nimblefix.core;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class OrganizationalFloors implements Serializable {

    String floorID;
    byte[] background_map=null;

    ArrayList<InventoryItem> inventories = new ArrayList<InventoryItem>();

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

    public ArrayList<InventoryItem> getInventories(){
        return this.inventories;
    }

    public InventoryItem getInventoryItem(int index){
        return inventories.get(index);
    }

    public InventoryItem getInventoryItem(InventoryItem.Location l){
        for(InventoryItem i : inventories){
            if(Math.abs(i.location.X-l.X)<=5&&Math.abs(i.location.Y-l.Y)<=5)
                return i;
        }
        return null;
    }

    public void addInventoryItem(InventoryItem item){
        inventories.add(item);
    }

    public int inventoryCount(){
        return inventories.size();
    }

    public void removeInventory(int index){
        inventories.remove(index);
    }

    public void removeInventory(InventoryItem inventoryItem){
        inventories.remove(inventoryItem);
    }
}
