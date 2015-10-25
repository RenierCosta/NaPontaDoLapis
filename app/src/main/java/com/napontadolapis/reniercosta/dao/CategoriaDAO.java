package com.napontadolapis.reniercosta.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.napontadolapis.reniercosta.DatabaseHelper;
import com.napontadolapis.reniercosta.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO extends ClasseBaseDAO {
    public CategoriaDAO(Context context) {
        super(context);
    }

    public boolean inserir(Categoria categoria){
        long resultado;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Categoria.DESCRICAO, categoria.getDescricao());
        values.put(DatabaseHelper.Categoria.TIPO, categoria.getTipo());
        resultado = getDb().insert(DatabaseHelper.Categoria.TABELA, null, values);
        return resultado != -1;
    }

    public boolean remover(Categoria categoria){
        String whereClause = DatabaseHelper.Categoria._ID + " = ?";
        String[] whereArgs = new String[]{categoria.getId().toString()};
        int removidos = getDb().delete(DatabaseHelper.Categoria.TABELA,
                whereClause, whereArgs);
        return removidos > 0;
    }

    public boolean atualizar(Categoria categoria){
        long resultado;
        String whereClause = DatabaseHelper.Categoria._ID + " = ?";
        String[] whereArgs = new String[]{categoria.getId().toString()};
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Categoria.DESCRICAO, categoria.getDescricao());
        values.put(DatabaseHelper.Categoria.TIPO, categoria.getTipo());
        resultado = getDb().update(DatabaseHelper.Categoria.TABELA, values, whereClause, whereArgs);
        return resultado != -1;
    }

    public List<Categoria> listarTodos(){
        Cursor cursor = getDb().query(DatabaseHelper.Categoria.TABELA,
                DatabaseHelper.Categoria.COLUNAS, null, null, null, null, null);

        List<Categoria> categorias = new ArrayList<Categoria>();

        while(cursor.moveToNext()){
            Categoria despesa = criarCategoria(cursor);
            categorias.add(despesa);
        }
        cursor.close();
        return categorias;
    }

    public Categoria buscarCategoriaPorId(Long idCategoria){
        String whereClause = DatabaseHelper.Categoria._ID + " = ?";
        String [] whereArgs = new String[]{idCategoria.toString()};

        Cursor cursor = getDb().query(DatabaseHelper.Categoria.TABELA,
                DatabaseHelper.Categoria.COLUNAS, whereClause, whereArgs, null, null, null);

        if(cursor.moveToNext()){
            Categoria categoria = criarCategoria(cursor);
            cursor.close();
            return categoria;
        }

        return null;
    }

    private Categoria criarCategoria(Cursor cursor){
        Long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Categoria._ID));
        String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Categoria.DESCRICAO));
        Integer tipo = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Categoria.TIPO));

        Categoria categoria = new Categoria(id, descricao, tipo);
        return categoria;
    }
}
