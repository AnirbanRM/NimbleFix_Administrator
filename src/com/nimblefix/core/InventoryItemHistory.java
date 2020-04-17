package com.nimblefix.core;

import java.io.Serializable;

public class InventoryItemHistory implements Serializable {

    public static class Type{
        public static final int PAST = 0;
        public static final int FUTURE = 1;
    }

    String oui,inventoryID,workDateTime,assignedTo;


}
