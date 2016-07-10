package com.webfactional.andrewk.akinventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;

public class AddItemReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String itemName = intent.getStringExtra("itemName");
        String itemDescription = intent.getStringExtra("itemDescription");

        InventoryItem item = new InventoryItem();
        item.setName(itemName);
        item.setDescription(itemDescription);

        DatabaseReference itemRef = InventoryItemManager.getItemsTable().push();
        item.setKey(itemRef.getKey());
        itemRef.setValue(item);
    }

    public AddItemReceiver()
    {
    }
}
