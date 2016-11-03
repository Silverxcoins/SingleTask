package com.example.sasha.singletask.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbHelper extends SQLiteOpenHelper {

    private static final Logger logger = LoggerFactory.getLogger(DbHelper.class);

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        logger.debug("onCreate()");

        db.execSQL(SqlQueries.CREATE_TABLE_TASK);
        db.execSQL(SqlQueries.CREATE_TABLE_CATEGORY);
        db.execSQL(SqlQueries.CREATE_TABLE_VARIANT);
        db.execSQL(SqlQueries.CREATE_TABLE_TASK_VARIANT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        logger.debug("onUpgrade");

    }
}
