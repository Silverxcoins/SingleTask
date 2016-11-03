package com.example.sasha.singletask.db.dataSets;

import android.database.Cursor;

import org.codehaus.jackson.JsonNode;

public class TaskVariantDataSet {
    private long task;
    private long variant;
    private boolean isDeleted;

    public TaskVariantDataSet(JsonNode json) {
        this.task = json.get("task").getLongValue();
        this.variant = json.get("variant").getLongValue();
        if (json.has("isDeleted")) this.isDeleted = json.get("isDeleted").getBooleanValue();
    }

    public TaskVariantDataSet(Cursor cursor) {
        this.task = cursor.getLong(cursor.getColumnIndex("task"));
        this.variant = cursor.getLong(cursor.getColumnIndex("variant"));
        this.isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
    }

    public long getTask() {
        return task;
    }

    public void setTask(long task) {
        this.task = task;
    }

    public long getVariant() {
        return variant;
    }

    public void setVariant(long variant) {
        this.variant = variant;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
