package com.webfactional.andrewk.akinventory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InventoryItemManager
{
    private static transient FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static transient DatabaseReference itemsTable = database.getReference("items");

    public static synchronized DatabaseReference getItemsTable()
    {
        return itemsTable;
    }
}
