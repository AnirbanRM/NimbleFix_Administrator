package com.nimblefix.core;

import java.io.Serializable;
import java.util.Calendar;

public class Category implements Serializable {
    String categoryString;
    String defaultTitle;
    String defaultDescription;
    String representationColor;
    String uniqueID;

    public Category(String categoryString){
        this.categoryString = categoryString;
        this.representationColor="#000000";
        Calendar i = Calendar.getInstance();
        this.uniqueID = ""+i.get(Calendar.DATE)+i.get(Calendar.MONTH)+i.get(Calendar.YEAR)+i.get(Calendar.HOUR)+i.get(Calendar.MINUTE)+i.get(Calendar.SECOND)+i.get(Calendar.MILLISECOND);
        this.defaultTitle="Empty title";
        this.defaultDescription="No description";
    }

    public String getCategoryString() {
        return categoryString;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    public String getRepresentationColor() {
        return representationColor;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setCategoryString(String categoryString) {
        this.categoryString = categoryString;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public void setDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

    public void setRepresentationColor(String representationColor) {
        this.representationColor = representationColor;
    }
}
