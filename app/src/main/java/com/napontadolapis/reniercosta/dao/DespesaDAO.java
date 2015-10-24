package com.napontadolapis.reniercosta.dao;

import android.content.Context;
import android.database.Cursor;
import android.widget.ListView;

import com.napontadolapis.reniercosta.DatabaseHelper;
import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Despesa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DespesaDAO extends NaPontaDoLapisDAO {

  private CategoriaDAO categoriaDAO;


    public DespesaDAO(Context context) {
        super(context);
        categoriaDAO = new CategoriaDAO(context);
    }

    public List<Despesa> listarDespesas(){
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

    private Despesa criarDespesa(Cursor cursor) {
        Long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Despesa._ID));
        String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Despesa.DESCRICAO));
        Date vencimento = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Despesa.VENCIMENTO)));
        Double valor = cursor.getDouble((cursor.getColumnIndex(DatabaseHelper.Despesa.VALOR)));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Despesa.STATUS));
        Categoria categoria = categoriaDAO.buscarCategoriaPorId(cursor.getColumnIndex(DatabaseHelper.Despesa.CATEGORIA_ID));

        Despesa despesa = new Despesa(id, descricao, vencimento, valor, status, categoria);
        return despesa;
    }


}
