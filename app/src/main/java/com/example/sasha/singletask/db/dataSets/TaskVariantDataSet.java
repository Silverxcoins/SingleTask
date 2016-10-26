package com.example.sasha.singletask.db.dataSets;

import android.database.Cursor;

import org.codehaus.jackson.JsonNode;

public class TaskVariantDataSet {
    private static Integer userId;

    public static void setUserId(int userId) {
        if (TaskVariantDataSet.userId == null) {
            TaskVariantDataSet.userId = userId;
        }
    }

    private int task;
    private int variant;
    private boolean isDeleted;

    public TaskVariantDataSet(JsonNode json) {
        this.task = json.get("task").getIntValue();
        this.variant = json.get("variant").getIntValue();
        if (json.has("isDeleted")) this.isDeleted = json.get("isDeleted").getBooleanValue();
    }

    public TaskVariantDataSet(Cursor cursor) {
        this.task = cursor.getInt(cursor.getColumnIndex("task"));
        this.variant = cursor.getInt(cursor.getColumnIndex("variant"));
        this.isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
    }


    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getVariant() {
        return variant;
    }

    public void setVariant(int variant) {
        this.variant = variant;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
