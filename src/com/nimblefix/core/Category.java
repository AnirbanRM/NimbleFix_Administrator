package com.nimblefix.core;

public class Category {
    String categoryString;
    String defaultName;
    String defaultDescription;
    String representationColor;

    Category(String categoryString){
        this.categoryString = categoryString;
        this.representationColor="#000000";
    }

    public String getCategoryString() {
        return categoryString;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    public String getRepresentationColor() {
        return representationColor;
    }

    public void setCategoryString(String categoryString) {
        this.categoryString = categoryString;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public void setDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

    public void setRepresentationColor(String representationColor) {
        this.representationColor = representationColor;
    }
}
