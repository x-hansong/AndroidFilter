package com.hansong.filter.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.hansong.filter.utils.Constants.T_BLACK_LIST;
import static com.hansong.filter.utils.Constants.T_WHITE_LIST;

/**
 * Created by xhans on 2016/7/3 0003.
 */
public class DBOpenHelper extends SQLiteOpenHelper{
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_BLACK_LIST + " (bid INTEGER PRIMARY KEY AUTOINCREMENT,phone VARCHAR(11))");
        db.execSQL("CREATE TABLE " + T_WHITE_LIST + " (wid INTEGER PRIMARY KEY AUTOINCREMENT,phone VARCHAR(11))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
