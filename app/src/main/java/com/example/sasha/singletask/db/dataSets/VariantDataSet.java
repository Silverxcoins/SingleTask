package com.example.sasha.singletask.db.dataSets;

import android.database.Cursor;

import org.codehaus.jackson.JsonNode;

public class VariantDataSet {

    private Long serverId;
    private Long id;
    private Long oldId;
    private String name;
    private long category;
    private boolean isDeleted;

    public VariantDataSet(JsonNode json) {
        if (json.has("id")) this.serverId = json.get("id").getLongValue();
        if (json.has("clientId")) this.oldId = json.get("clientId").getLongValue();
        this.name = json.get("name").getTextValue();
        this.category = json.get("category").getIntValue();
        if (json.has("isDeleted")) this.isDeleted = json.get("isDeleted").getBooleanValue();
    }

    public VariantDataSet(Cursor cursor) {
        this.serverId = cursor.getLong(cursor.getColumnIndex("serverId"));
        this.id = cursor.getLong(cursor.getColumnIndex("id"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.category = cursor.getInt(cursor.getColumnIndex("category"));
        this.isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }
}
