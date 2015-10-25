package com.napontadolapis.reniercosta.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.napontadolapis.reniercosta.DatabaseHelper;
import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Despesa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DespesaDAO extends ClasseBaseDAO {

  private CategoriaDAO categoriaDAO;


    public DespesaDAO(Context context) {
        super(context);
        categoriaDAO = new CategoriaDAO(context);
    }

    public boolean inserir(Despesa despesa){
        long resultado;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Despesa.DESCRICAO, despesa.getDescricao());
        values.put(DatabaseHelper.Despesa.VENCIMENTO, despesa.getVencimento().getTime());
        values.put(DatabaseHelper.Despesa.VALOR, despesa.getValor());
        values.put(DatabaseHelper.Despesa.STATUS, despesa.getStatus());
        values.put(DatabaseHelper.Despesa.CATEGORIA_ID, despesa.getCategoria().getId());
        resultado = getDb().insert(DatabaseHelper.Despesa.TABELA, null, values);
        return resultado != -1;
    }

    public boolean remover(Long idDespesa){
        String whereClause = DatabaseHelper.Despesa._ID + " = ?";
        String[] whereArgs = new String[]{idDespesa.toString()};
        int removidos = getDb().delete(DatabaseHelper.Despesa.TABELA,
                whereClause, whereArgs);
        return removidos > 0;
    }

    public boolean remover(Despesa despesa){
        return remover(despesa.getId());
    }

    public boolean atualizar(Despesa despesa){
        long resultado;
        String whereClause = DatabaseHelper.Despesa._ID + " = ?";
        String[] whereArgs = new String[]{despesa.getId().toString()};
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Despesa.DESCRICAO, despesa.getDescricao());
        values.put(DatabaseHelper.Despesa.VENCIMENTO, despesa.getVencimento().getTime());
        values.put(DatabaseHelper.Despesa.VALOR, despesa.getValor());
        values.put(DatabaseHelper.Despesa.STATUS, despesa.getStatus());
        values.put(DatabaseHelper.Despesa.CATEGORIA_ID, despesa.getCategoria().getId());
        resultado = getDb().update(DatabaseHelper.Despesa.TABELA, values, whereClause, whereArgs);
        return resultado != -1;
    }

    public List<Despesa> listarTodos(){
        Cursor cursor = getDb().query(DatabaseHelper.Despesa.TABELA,
                DatabaseHelper.Despesa.COLUNAS, null, null, null, null, null);

        List<Despesa> despesas = new ArrayList<Despesa>();

        while(cursor.moveToNext()){
            Despesa despesa = criarDespesa(cursor);
            despesas.add(despesa);
        }
        cursor.close();
        return despesas;
    }

    public Despesa buscarDespesaPorId(Long idDespesa){
        Cursor cursor = getDb().query(DatabaseHelper.Despesa.TABELA,
                DatabaseHelper.Despesa.COLUNAS,
                DatabaseHelper.Despesa._ID + " = ?",
                new String[]{ idDespesa.toString() },
                null, null, null);

        if(cursor.moveToNext()){
            Despesa despesa = criarDespesa(cursor);
            cursor.close();
            return despesa;
        }

        return null;
    }

    private Despesa criarDespesa(Cursor cursor) {
        Long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Despesa._ID));
        String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Despesa.DESCRICAO));
        Date vencimento = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Despesa.VENCIMENTO)));
        Double valor = cursor.getDouble((cursor.getColumnIndex(DatabaseHelper.Despesa.VALOR)));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Despesa.STATUS));
        Categoria categoria = categoriaDAO.buscarCategoriaPorId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Despesa.CATEGORIA_ID)));

        Despesa despesa = new Despesa(id, descricao, vencimento, valor, status, categoria);
        return despesa;
    }

}
