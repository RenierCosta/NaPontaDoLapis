package com.napontadolapis.reniercosta.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ClasseBaseDAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public ClasseBaseDAO(Context context){
        helper = new DatabaseHelper(context);
    }

    protected SQLiteDatabase getDb() {
        if (db == null) {
            db = helper.getWritableDatabase();
        }
        return db;
    }

    public void close(){
        helper.close();
    }
}
