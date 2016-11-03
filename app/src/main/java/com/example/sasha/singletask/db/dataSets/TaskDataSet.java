package com.example.sasha.singletask.db.dataSets;


import android.database.Cursor;
import android.util.Log;

import com.example.sasha.singletask.helpers.Utils;

import org.codehaus.jackson.JsonNode;

public class TaskDataSet {

    private Long serverId;
    private Long id;
    private Long oldId;
    private long user;
    private String name;
    private String comment;
    private String date;
    private int time;
    private boolean isUpdated;
    private boolean isDeleted;
    private String lastUpdate;

    public TaskDataSet(JsonNode json) {
        if (json.has("id")) this.serverId = json.get("id").getLongValue();
        if (json.has("clientId")) this.oldId = json.get("clientId").getLongValue();
        this.name = json.get("name").getTextValue();
        if (json.has("comment")) this.comment = json.get("comment").getTextValue();
        if (json.has("date")) this.date = json.get("date").getTextValue();
        this.time = json.get("time").getIntValue();
        this.user = Utils.getUserId();
        if (json.has("isDeleted")) this.isDeleted = json.get("isDeleted").getBooleanValue();
        if (json.has("isUpdated")) this.isUpdated = json.get("isUpdated").getBooleanValue();
        this.lastUpdate = json.get("lastUpdate").getTextValue();
    }

    public TaskDataSet(Cursor cursor) {
        this.serverId = cursor.getLong(cursor.getColumnIndex("serverId"));
        this.id = cursor.getLong(cursor.getColumnIndex("id"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.comment = cursor.getString(cursor.getColumnIndex("comment"));
        this.date = cursor.getString(cursor.getColumnIndex("date"));
        this.time = cursor.getInt(cursor.getColumnIndex("time"));
        this.user = Utils.getUserId();
        this.isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
        this.isUpdated = (cursor.getInt(cursor.getColumnIndex("isUpdated")) == 1);
        this.lastUpdate = cursor.getString(cursor.getColumnIndex("lastUpdate"));
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

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }
}
