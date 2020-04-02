package com.nimblefix.ControlMessages;

import com.nimblefix.core.Complaint;

import java.io.Serializable;
import java.util.ArrayList;

public class ComplaintMessage implements Serializable {

    ArrayList<Complaint> complaints = new ArrayList<Complaint>();
    String body;

    public ComplaintMessage(Complaint complaint){ complaints.add(complaint); }

    public ComplaintMessage(ArrayList<Complaint> complaints){
        this.complaints = complaints;
    }

    public Complaint getComplaint() {
        return complaints.get(0);
    }

    public void setComplaint(Complaint complaint) {
        this.complaints.set(0,complaint);
    }

    public ArrayList<Complaint> getComplaints(){
        return this.complaints;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
