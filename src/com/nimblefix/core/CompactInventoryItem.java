package com.nimblefix.core;

import java.io.Serializable;

public class CompactInventoryItem implements Serializable {

    String id;
    String title;
    String organizationName;

    public CompactInventoryItem(String id, String title, String organizationName) {
        this.id = id;
        this.title = title;
        this.organizationName = organizationName;
    }

    public String getTitle() {
        return this.title;
    }

    public String getId() {
        return this.id;
    }

    public String getOrganization_Name() {
        return this.organizationName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
