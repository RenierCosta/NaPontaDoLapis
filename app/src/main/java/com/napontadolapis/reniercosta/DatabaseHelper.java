package com.napontadolapis.reniercosta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "NaPontaDoLapis";
    private static int VERSAO = 1;

    public DatabaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE despesas (_id INTEGER PRIMARY KEY," +
                " descricao TEXT, vencimento DATE," +
                " valor DOUBLE," +
                " status TEXT," +
                " categoria_id INTEGER);");

        db.execSQL("CREATE TABLE categoria (_id INTEGER PRIMARY KEY," +
                " descricao TEXT," +
                " tipo INTEGER);");

        db.execSQL("CREATE TABLE receita (_id INTEGER PRIMARY KEY," +
                " descricao TEXT, data_recebimento DATE," +
                " categoria_id INTEGER," +
                " valor DOUBLE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
