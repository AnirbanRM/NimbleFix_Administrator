package com.nimblefix.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class WorkerFilter {

    ArrayList<Worker> workers = new ArrayList<Worker>();

    public WorkerFilter(ArrayList<Worker> workers) {
        for(Worker w : workers)
            this.workers.add(w);

    }

    public ArrayList<Worker> getFilteredResult() {
        return this.workers;
    }

    public void setArrayList(ArrayList<Worker> workers){
        for(Worker w : workers)
            this.workers.add(w);
    }

    public void filterByEmpID(String empID) {
        if (empID.trim().equals("")) return;
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            if (!listIterator.next().getEmpID().toLowerCase().contains(empID.toLowerCase()))
                listIterator.remove();
        }
    }

    public void filterByEmpID(String empID1, String empID2) {
        if (empID1.trim().equals("") || empID2.trim().equals("")) return;
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            String empID = listIterator.next().getEmpID().toLowerCase();
            empID1 = empID1.toLowerCase();
            empID2 = empID2.toLowerCase();
            if (!((empID.compareTo(empID1) >= 0 && (empID.compareTo(empID2) <= 0)) || (empID.compareTo(empID1) <= 0 && (empID.compareTo(empID2) >= 0))))
                listIterator.remove();
        }
    }

    public void filterByName(String name) {
        if (name.trim().equals("")) return;
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            if (!listIterator.next().getName().toLowerCase().contains(name.toLowerCase()))
                listIterator.remove();
        }
    }

    public void filterByEmail(String email) {
        if (email.trim().equals("")) return;
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            if (!listIterator.next().getEmail().toLowerCase().contains(email.toLowerCase()))
                listIterator.remove();
        }
    }

    public void filterByMobile(String mobile) {
        if (mobile.trim().equals("")) return;
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            if (!listIterator.next().getMobile().contains(mobile))
                listIterator.remove();
        }
    }

    public void filterByDesignation(String designation) {
        if (designation.trim().equals("Any Designation")) return;
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            if (!listIterator.next().getDesignation().toLowerCase().contains(designation.toLowerCase()))
                listIterator.remove();
        }
    }

    public void filterByDOB(String doB) {
        if (doB.trim().equals("")) return;
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            if (!listIterator.next().getDoB().contains(doB))
                listIterator.remove();
        }
    }

    public void filterByDOJ(String doJ) {
        if (doJ.trim().equals("")) return;
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            if (!listIterator.next().getDoJ().contains(doJ))
                listIterator.remove();
        }
    }

    public void filterByDOB(String doB1, String doB2) {
        if (doB1.trim().equals("") || doB2.trim().equals("")) return;
        SimpleDateFormat dateGenerator = new SimpleDateFormat("dd/MM/yyyy");
        Date date1;
        Date date2;
        try {
            date1 = dateGenerator.parse(doB1);
            date2 = dateGenerator.parse(doB2);
        } catch (Exception e) {
            return;
        }
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            Date date;
            try {
                date = dateGenerator.parse(listIterator.next().getDoB());
            } catch (ParseException e) { return; }
            if (!((date.compareTo(date1) >= 0 && (date.compareTo(date2) <= 0)) || (date.compareTo(date1) <= 0 && (date.compareTo(date2) >= 0))))
                listIterator.remove();
        }
    }

    public void filterByDOJ(String doJ1, String doJ2) {
        if (doJ1.trim().equals("") || doJ2.trim().equals("")) return;
        SimpleDateFormat dateGenerator = new SimpleDateFormat("dd/MM/yyyy");
        Date date1;
        Date date2;
        try {
            date1 = dateGenerator.parse(doJ1);
            date2 = dateGenerator.parse(doJ2);
        } catch (Exception e) {
            return;
        }
        Iterator<Worker> listIterator = workers.iterator();
        while (listIterator.hasNext()) {
            Date date;
            try {
                date = dateGenerator.parse(listIterator.next().getDoJ());
            } catch (ParseException e) { return; }
            if (!((date.compareTo(date1) >= 0 && (date.compareTo(date2) <= 0)) || (date.compareTo(date1) <= 0 && (date.compareTo(date2) >= 0))))
                listIterator.remove();
        }
    }
}