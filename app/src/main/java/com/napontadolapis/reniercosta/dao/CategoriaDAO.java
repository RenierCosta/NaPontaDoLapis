package com.napontadolapis.reniercosta.dao;

import android.content.Context;
import android.database.Cursor;

import com.napontadolapis.reniercosta.DatabaseHelper;
import com.napontadolapis.reniercosta.model.Categoria;

/**
 * Created by Renier on 24/10/2015.
 */
public class CategoriaDAO extends NaPontaDoLapisDAO {
    public CategoriaDAO(Context context) {
        super(context);
    }

    public Categoria buscarCategoriaPorId(Integer idCategoria){
        Cursor cursor = getDb().query(DatabaseHelper.Categoria.TABELA,
                DatabaseHelper.Categoria.COLUNAS,
                DatabaseHelper.Categoria._ID + " = ?",
                new String[]{ idCategoria.toString() },
                null, null, null);
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
