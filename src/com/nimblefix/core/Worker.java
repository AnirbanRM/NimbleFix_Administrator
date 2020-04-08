package com.nimblefix.core;

import java.io.Serializable;

public class Worker implements Serializable,Cloneable {

    private String empID,name,email,mobile,designation,doB,doJ;
    byte[] dP = null;

    public Worker(){
        empID=name=email=mobile=designation=doB=doJ="";
    }

    public Worker(Worker worker){
        setEmpID(worker.getEmpID());
        setName(worker.getName());
        setEmail(worker.getEmail());
        setMobile(worker.getMobile());
        setDesignation(worker.getDesignation());
        setDoB(worker.getDoB());
        setDoJ(worker.getDoJ());
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDoB() {
        return doB;
    }

    public void setDoB(String doB) {
        this.doB = doB;
    }

    public String getDoJ() {
        return doJ;
    }

    public void setDoJ(String doJ) {
        this.doJ = doJ;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setDP(byte[] bytes) {
        this.dP = bytes;
    }

    public byte[] getDP() {
        return dP;
    }
}
