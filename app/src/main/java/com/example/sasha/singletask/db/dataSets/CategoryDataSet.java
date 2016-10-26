package com.example.sasha.singletask.db.dataSets;

import android.database.Cursor;

import org.codehaus.jackson.JsonNode;

public class CategoryDataSet {
    private static Integer userId;

    public static void setUserId(int userId) {
        if (CategoryDataSet.userId == null) {
            CategoryDataSet.userId = userId;
        }
    }

    private Integer id;
    private String name;
    private int parent;
    private int user;
    private boolean isDeleted;
    private boolean isUpdated;
    private String lastUpdate;

    public CategoryDataSet(JsonNode json) {
        if (json.has("id")) this.id = json.get("id").getIntValue();
        this.name = json.get("name").getTextValue();
        if (json.has("parent")) this.parent = json.get("parent").getIntValue();
        this.user = CategoryDataSet.userId;
        if (json.has("isDeleted")) this.isDeleted = json.get("isDeleted").getBooleanValue();
        if (json.has("isUpdated")) this.isUpdated = json.get("isUpdated").getBooleanValue();
        this.lastUpdate = json.get("lastUpdate").getTextValue();
    }

    public CategoryDataSet(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex("serverId"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.parent = cursor.getInt(cursor.getColumnIndex("parent"));
        this.isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
        this.isUpdated = (cursor.getInt(cursor.getColumnIndex("isUpdated")) == 1);
        this.user = CategoryDataSet.userId;
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

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
