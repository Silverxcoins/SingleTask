package com.example.sasha.singletask.db.dataSets;

import android.database.Cursor;

import com.example.sasha.singletask.helpers.Utils;

import org.codehaus.jackson.JsonNode;

public class CategoryDataSet {

    private Long serverId;
    private Long id;
    private Long oldId;
    private String name;
    private long parent;
    private long user;
    private boolean isDeleted;
    private boolean isUpdated;
    private String lastUpdate;

    public CategoryDataSet(JsonNode json) {
        if (json.has("id")) this.serverId = json.get("id").getLongValue();
        if (json.has("clientId")) this.oldId = json.get("clientId").getLongValue();
        this.name = json.get("name").getTextValue();
        if (json.has("parent")) this.parent = json.get("parent").getIntValue();
        this.user = Utils.getUserId();
        if (json.has("isDeleted")) this.isDeleted = json.get("isDeleted").getBooleanValue();
        if (json.has("isUpdated")) this.isUpdated = json.get("isUpdated").getBooleanValue();
        this.lastUpdate = json.get("lastUpdate").getTextValue();
    }

    public CategoryDataSet(Cursor cursor) {
        this.serverId = cursor.getLong(cursor.getColumnIndex("serverId"));
        this.id = cursor.getLong(cursor.getColumnIndex("id"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.parent = cursor.getInt(cursor.getColumnIndex("parent"));
        this.isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
        this.isUpdated = (cursor.getInt(cursor.getColumnIndex("isUpdated")) == 1);
        this.user = Utils.getUserId();
        this.lastUpdate = cursor.getString(cursor.getColumnIndex("lastUpdate"));
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
