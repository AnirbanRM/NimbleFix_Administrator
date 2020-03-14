package com.nimblefix.core;

import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;

import java.util.ArrayList;
import java.util.Calendar;

public class Organization {

    String oui;
    String organization_Name;
    ArrayList<OrganizationalFloors> floors;

    public Organization(String organization_Name){
        this.organization_Name = organization_Name;
        floors = new ArrayList<OrganizationalFloors>();
    }

    public ArrayList<OrganizationalFloors> getFloors(){
        return floors;
    }

    public boolean floorExists(String floorID){
        for(OrganizationalFloors f : floors)
            if(f.floorID.equals(floorID))
                return true;
        return false;
    }

    public String getUniqueID(){
        Calendar i = Calendar.getInstance();
        return ""+i.get(Calendar.DATE)+i.get(Calendar.MONTH)+i.get(Calendar.YEAR)+i.get(Calendar.HOUR)+i.get(Calendar.MINUTE)+i.get(Calendar.SECOND)+i.get(Calendar.MILLISECOND);
    }

    public void addFloor(OrganizationalFloors floor){
        floors.add(floor);
    }

    public void removeFloor(String floor_id){
        OrganizationalFloors temp=null;
        for(int i = 0; i<floors.size();i++){
            if(temp.floorID.equals(floor_id))
                temp = floors.get(i);
        }
        floors.remove(temp);
    }

    public void removeFloor(int index){
        if(index>=floors.size())return;
        floors.remove(index);
    }

    public OrganizationalFloors getFloor(int index){
        return floors.get(index);
    }

    public OrganizationalFloors getFloor(String floorID){
        for(OrganizationalFloors f : floors){
            if(f.floorID.equals(floorID))return f;
        }
        return null;
    }

    public int getFloorscount(){
        return floors.size();
    }
}
