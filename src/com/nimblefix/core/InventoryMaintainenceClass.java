package com.nimblefix.core;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InventoryMaintainenceClass implements Serializable {

    public static final String DATEPATTERN = "dd/MM/yyyy";

    public static class Type{
        public static int WEEKLY = 1;
        public static int MONTHLY = 2;
        public static int QUARTERLY = 3;
        public static int HALF_YEARLY = 4;
        public static int YEARLY = 5;
    }

    String OUI;
    String inventoryID;
    double frequency;
    String lastMaintainenceDate;
    int type;

    public InventoryMaintainenceClass(String OUI,String inventoryID){
        this.OUI = OUI;
        this.inventoryID = inventoryID;
    }

    public InventoryMaintainenceClass(String inventoryID){
        this.inventoryID = inventoryID;
    }

    public String getOUI() {
        return OUI;
    }

    public void setOUI(String OUI) {
        this.OUI = OUI;
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public String getLastMaintainenceDate() {
        return lastMaintainenceDate;
    }

    public void setLastMaintainenceDate(String lastMaintainenceDate) {
        this.lastMaintainenceDate = lastMaintainenceDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<LocalDateTime> getMaintainanceDates(int year) {
        Date date = null;
        try {
             date = new SimpleDateFormat(DATEPATTERN).parse(lastMaintainenceDate);
        }catch (Exception e){ date = new Date(); }
        ArrayList<LocalDateTime> dates = new ArrayList<LocalDateTime>();

        long u = (long) (24L * 60L * 60L * 1000L * frequency);
        if(type == Type.WEEKLY)
            u *= 7F;
        else if(type == Type.MONTHLY)
            u *= 30F;
        else if(type == Type.QUARTERLY)
            u *= (3F*30F);
        else if(type == Type.HALF_YEARLY)
            u *= (6F*30F);
        else if(type == Type.YEARLY)
            u *= (12F*30F);

        Date temp = date;
        while(true){
            Date newDate =  new Date(temp.getTime()+u);
            LocalDateTime ld = LocalDateTime.ofInstant(newDate.toInstant(), ZoneId.systemDefault());
            temp = newDate;
            if(ld.getYear()==year)
                dates.add(ld);
            else if(ld.getYear()>year)
                break;
        }
        return dates;
    }

    public boolean isExpired(){
        Date lastMDt = null;
        Date currentDate = new Date(); //currentDate

        try { lastMDt = new SimpleDateFormat(DATEPATTERN).parse(lastMaintainenceDate); } catch (ParseException e) { }
        long difference = currentDate.getTime()-lastMDt.getTime();

        long u = (long) (24L * 60L * 60L * 1000L * frequency);
        if(type == Type.WEEKLY)
            u *= 7F;
        else if(type == Type.MONTHLY)
            u *= 30F;
        else if(type == Type.QUARTERLY)
            u *= (3F*30F);
        else if(type == Type.HALF_YEARLY)
            u *= (6F*30F);
        else if(type == Type.YEARLY)
            u *= (12F*30F);

        if(difference>u)return true;
        else return false;
    }
}
