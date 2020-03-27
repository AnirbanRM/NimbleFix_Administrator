package com.nimblefix.ControlMessages;

import com.nimblefix.core.Complaint;

import java.io.Serializable;

public class ComplaintMessage implements Serializable {

    Complaint complaint;
    String body;

    public ComplaintMessage(Complaint complaint){
        this.complaint=complaint;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
