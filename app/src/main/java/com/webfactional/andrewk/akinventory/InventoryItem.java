package com.webfactional.andrewk.akinventory;

import android.location.Location;

import java.io.Serializable;

public class InventoryItem implements Serializable
{
    public String getKey()
    {
        return key;
    }

    private String key = null;

    public void setKey(String value)
    {
        key = value;
    }

    public String getName()
    {
        return name;
    }

    private String name;

    public void setName(String value)
    {
        name = value;
    }

    public String getDescription()
    {
        return description;
    }

    private String description;

    public void setDescription(String value)
    {
        description = value;
    }

    public String getImageUri()
    {
        return imageUri;
    }

    private String imageUri = null;

    public void setImageUri(String value)
    {
        imageUri = value;
    }

    public LocationSerializable getLocation()
    {
        return location;
    }

    private LocationSerializable location = null;

    public void setLocation(LocationSerializable value)
    {
        location = value;
    }

    public InventoryItem()
    {

    }
}
