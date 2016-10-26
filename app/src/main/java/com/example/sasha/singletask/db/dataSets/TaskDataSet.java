package com.example.sasha.singletask.db.dataSets;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import org.codehaus.jackson.JsonNode;

import java.security.Timestamp;

public class TaskDataSet {
    private static Integer userId;

    public static void setUserId(int userId) {
        if (TaskDataSet.userId == null) {
            TaskDataSet.userId = userId;
        }
    }

    int id;
    int user;
    String name;
    String comment;
    String date;
    int time;
    boolean isUpdated;
    boolean isDeleted;
    String lastUpdate;

    public TaskDataSet(JsonNode json) {
        if (json.has("id")) this.id = json.get("id").getIntValue();
        this.name = json.get("name").getTextValue();
        if (json.has("comment")) this.comment = json.get("comment").getTextValue();
        if (json.has("date")) this.date = json.get("date").getTextValue();
        this.time = json.get("time").getIntValue();
        this.user = TaskDataSet.userId;
        if (json.has("isDeleted")) this.isDeleted = json.get("isDeleted").getBooleanValue();
        if (json.has("isUpdated")) this.isUpdated = json.get("isUpdated").getBooleanValue();
        this.lastUpdate = json.get("lastUpdate").getTextValue();
    }

    public TaskDataSet(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex("serverId"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.comment = cursor.getString(cursor.getColumnIndex("comment"));
        this.date = cursor.getString(cursor.getColumnIndex("date"));
        this.time = cursor.getInt(cursor.getColumnIndex("time"));
        this.user = TaskDataSet.userId;
        this.isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
        this.isUpdated = (cursor.getInt(cursor.getColumnIndex("isUpdated")) == 1);
        this.lastUpdate = cursor.getString(cursor.getColumnIndex("lastUpdate"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
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

    public void setIsDeleted(boolean deleted) {
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
}
