package com.nimblefix.ControlMessages;

import com.nimblefix.core.Organization;

import java.io.Serializable;
import java.util.ArrayList;

public class OrganizationsExchangerMessage implements Serializable {

    ArrayList<Organization> organizations;
    String organizationOwner;

    public OrganizationsExchangerMessage(String organizationOwner){
        this.organizationOwner = organizationOwner;
    }

    public ArrayList<Organization> getOrganizations() {
        return organizations;
    }

    public Organization getOrganization(String OUI){
        for(Organization o : this.getOrganizations())
            if(o.getOui().equals(OUI))
                return o;
        return null;
    }

    public void setOrganizations(ArrayList<Organization> organizations) {
        this.organizations = organizations;
    }

    public void addOrganization(Organization organization){
        this.organizations.add(organization);
    }

    public void deleteOrganization(Organization organization){
        this.organizations.remove(organization);
    }

    public void  deleteOrganization(int i){
        this.organizations.remove(i);
    }
}
