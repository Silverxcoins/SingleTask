package com.example.sasha.singletask.db;

public class SqlQueries {
    public static final String CREATE_TABLE_TASK = "CREATE TABLE Task ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "serverId INTEGER," +
            "name TEXT, " +
            "comment text, " +
            "date TEXT, " +
            "time INTEGER, " +
            "lastUpdate TEXT," +
            "isDeleted INTEGER, " +
            "isUpdated INTEGER );";
    public static final String CREATE_TABLE_CATEGORY = "CREATE TABLE Category ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "serverId INTEGER, " +
            "name TEXT, " +
            "parent INTEGER, " +
            "lastUpdate TEXT," +
            "isDeleted INTEGER," +
            "isUpdated INTEGER );";
    public static final String CREATE_TABLE_VARIANT = "CREATE TABLE Variant ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "serverId INTEGER, " +
            "name TEXT, " +
            "category INTEGER," +
            "isDeleted INTEGER );";
    public static final String CREATE_TABLE_TASK_VARIANT = "CREATE TABLE VariantTask ( " +
            "task INTEGER, " +
            "variant integer, " +
            "isDeleted INTEGER );";
    public static final String GET_VARIANT_BY_TASK_AND_CATEGORY = "SELECT Variant.* " +
            "FROM Variant JOIN VariantTask ON Variant.id=VariantTask.variant " +
            "WHERE VariantTask.task=? AND Variant.category=? AND VariantTask.isDeleted != 1";
    public static final String GET_TASKS_VARIANTS_BY_TASK_AND_CATEGORY = "SELECT VariantTask.* " +
            "FROM VariantTask JOIN Variant ON Variant.id = VariantTask.variant " +
            "WHERE VariantTask.task=? AND Variant.category=?";
    public static final String UPDATE_TASK_TIME_AND_LAST_UPDATE = "UPDATE Task SET time=time+?, " +
            "lastUpdate=? WHERE id=?";
}
