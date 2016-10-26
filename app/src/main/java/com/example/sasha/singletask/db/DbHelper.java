package com.example.sasha.singletask.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SqlQueries.CREATE_TABLE_TASK);
        db.execSQL(SqlQueries.CREATE_TABLE_CATEGORY);
        db.execSQL(SqlQueries.CREATE_TABLE_VARIANT);
        db.execSQL(SqlQueries.CREATE_TABLE_TASK_VARIANT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
