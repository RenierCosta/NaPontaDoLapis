package com.napontadolapis.reniercosta.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "NaPontaDoLapis";
    private static int VERSAO = 1;

    public static class Despesa {
        public static final String TABELA = "despesa";
        public static final String _ID = "_id";
        public static final String DESCRICAO = "descricao";
        public static final String VENCIMENTO = "vencimento";
        public static final String VALOR = "valor";
        public static final String STATUS = "status";
        public static final String CATEGORIA_ID = "categoria_id";

        public static final String[] COLUNAS = new String[]{
                _ID, DESCRICAO, VENCIMENTO, VALOR,
                STATUS, CATEGORIA_ID };
    }

    public static class Categoria{
        public static final String TABELA = "categoria";
        public static final String _ID = "_id";
        public static final String DESCRICAO = "descricao";
        public static final String TIPO = "tipo";

        public static final String[] COLUNAS = new String[]{
            _ID, DESCRICAO, TIPO };
    }

    public DatabaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE despesa (_id INTEGER PRIMARY KEY," +
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