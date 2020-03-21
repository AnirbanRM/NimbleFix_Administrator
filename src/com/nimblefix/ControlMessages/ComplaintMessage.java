package com.nimblefix.ControlMessages;

import com.nimblefix.core.Complaint;

import java.io.Serializable;

public class ComplaintMessage extends Complaint implements Serializable {

    Complaint complaint;

    ComplaintMessage(Complaint complaint){
        this.complaint=complaint;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }
}
