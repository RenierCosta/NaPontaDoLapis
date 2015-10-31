package com.napontadolapis.reniercosta.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Constantes;
import com.napontadolapis.reniercosta.model.Receita;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class ReceitaDAO extends ClasseBaseDAO {
    private CategoriaDAO categoriaDAO;

    public ReceitaDAO(Context context) {
        super(context);
        categoriaDAO = new CategoriaDAO(context);
    }

    private ContentValues obterValuesDaReceita(Receita receita){
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_BANCO);
        values.put(DatabaseHelper.Receita.DESCRICAO, receita.getDescricao());
        values.put(DatabaseHelper.Receita.RECEBIMENTO, dateFormat.format(receita.getRecebimento()));
        values.put(DatabaseHelper.Receita.VALOR, receita.getValor());
        values.put(DatabaseHelper.Receita.STATUS, receita.getStatus());
        values.put(DatabaseHelper.Receita.CATEGORIA_ID, receita.getCategoria().getId());
        values.put(DatabaseHelper.Receita.DATA, dateFormat.format(receita.getData()));

        return values;
    }

    private List<Receita> obterListaDeDepesa(Cursor cursor){
        List<Receita> despesas = new ArrayList<Receita>();

        while(cursor.moveToNext()){
            Receita receita = criarReceita(cursor);
            despesas.add(receita);
        }
        cursor.close();

        return despesas;
    }

    private Receita criarReceita(Cursor cursor) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_BANCO);

        Long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Receita._ID));
        String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Receita.DESCRICAO));
        Date recebimento = null;
        Date data = null;
        try {
            recebimento = dateFormat.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Receita.RECEBIMENTO)));
            data = dateFormat.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Receita.DATA)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Double valor = cursor.getDouble((cursor.getColumnIndex(DatabaseHelper.Receita.VALOR)));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Receita.STATUS));
        Categoria categoria = categoriaDAO.buscarCategoriaPorId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Receita.CATEGORIA_ID)));


        Receita receita = new Receita(id, descricao, data, recebimento, categoria, status, valor);
        return receita;
    }

    public boolean inserir(Receita receita){
        long resultado;
        resultado = getDb().insert(DatabaseHelper.Receita.TABELA, null, obterValuesDaReceita(receita));
        return resultado != -1;
    }

    public boolean remover(Long idReceita){
        String whereClause = DatabaseHelper.Receita._ID + " = ?";
        String[] whereArgs = new String[]{idReceita.toString()};
        int removidos = getDb().delete(DatabaseHelper.Receita.TABELA,
                whereClause, whereArgs);
        return removidos > 0;
    }

    public boolean remover(Receita receita){
        return remover(receita.getId());
    }

    public boolean atualizar(Receita receita){
        long resultado;
        String whereClause = DatabaseHelper.Receita._ID + " = ?";
        String[] whereArgs = new String[]{receita.getId().toString()};
        resultado = getDb().update(DatabaseHelper.Receita.TABELA, obterValuesDaReceita(receita), whereClause, whereArgs);
        return resultado != -1;
    }

    public List<Receita> listarTodos(){
        Cursor cursor = getDb().query(DatabaseHelper.Receita.TABELA,
                DatabaseHelper.Receita.COLUNAS, null, null, null, null, null);

        return obterListaDeDepesa(cursor);
    }

    public List<Receita> listarTodosPorFiltro(String selection, String[] selecionArgs){
        Cursor cursor = getDb().query(DatabaseHelper.Receita.TABELA,
                DatabaseHelper.Receita.COLUNAS, selection, selecionArgs, null, null, null);

        return obterListaDeDepesa(cursor);
    }

    public Receita buscarReceitaPorId(Long idReceita){
        Cursor cursor = getDb().query(DatabaseHelper.Receita.TABELA,
                DatabaseHelper.Receita.COLUNAS,
                DatabaseHelper.Receita._ID + " = ?",
                new String[]{ idReceita.toString() },
                null, null, null);

        if(cursor.moveToNext()){
            Receita receita = criarReceita(cursor);
            cursor.close();
            return receita;
        }

        return null;
    }
}

