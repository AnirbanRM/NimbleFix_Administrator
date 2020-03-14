package com.nimblefix.core;

import java.util.ArrayList;

public class OrganizationalFloors {

    String floorID;
    ArrayList<InventoryItem> inventories = new ArrayList<InventoryItem>();

    public OrganizationalFloors(String floorID){
        this.floorID = floorID;
    }

    public String getFloorID() {
        return floorID;
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
