package com.example.sasha.singletask.db.dataSets;

import android.database.Cursor;

import org.codehaus.jackson.JsonNode;

public class VariantDataSet {

    private Integer serverId;
    private Integer id;
    private Integer oldId;
    private String name;
    private int category;
    private boolean isDeleted;

    public VariantDataSet(JsonNode json) {
        if (json.has("id")) this.serverId = json.get("id").getIntValue();
        if (json.has("clientId")) this.oldId = json.get("clientId").getIntValue();
        this.name = json.get("name").getTextValue();
        this.category = json.get("category").getIntValue();
        if (json.has("isDeleted")) this.isDeleted = json.get("isDeleted").getBooleanValue();
    }

    public VariantDataSet(Cursor cursor) {
        this.serverId = cursor.getInt(cursor.getColumnIndex("serverId"));
        this.id = cursor.getInt(cursor.getColumnIndex("id"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.category = cursor.getInt(cursor.getColumnIndex("category"));
        this.isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getOldId() {
        return oldId;
    }

    public void setOldId(Integer oldId) {
        this.oldId = oldId;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }
}
