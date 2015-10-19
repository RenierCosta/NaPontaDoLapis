package com.napontadolapis.reniercosta;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DespesaCadastroListActivity extends ListActivity implements OnItemClickListener {

    private DatabaseHelper helper;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String[] de = { "descricao" };
        int[] para = { R.id.edtDescricaoDespesa};

        SimpleAdapter adapter = new SimpleAdapter(this,
                listarDespesas(), android.R.layout.simple_list_item_1, de, para);

        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view;
        String mensagem = "Despesa selecionada: " + textView.getText();
        Toast.makeText(getApplicationContext(), mensagem,
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DespesaEdicaoActivity.class));
    }

    private List<Map<String, Object>> listarDespesas() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, descricao, vencimento, " +
                        "valor, status, categoria_id FROM despesas",
                null);

        cursor.moveToFirst();

        ArrayList<Map<String, Object>> depesas = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < cursor.getCount(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();

            String id = cursor.getString(cursor.getColumnIndex("_id"));
            String descricao = cursor.getString(cursor.getColumnIndex("descricao"));
            long vencimento = cursor.getLong(cursor.getColumnIndex("vencimento"));
            String status = cursor.getString(cursor.getColumnIndex("status"));
            int categoria_Id = cursor.getInt(cursor.getColumnIndex("categoria_id"));
            double valor = cursor.getDouble(cursor.getColumnIndex("valor"));

            Date vencimentoDate = new Date(vencimento);

            item.put("id", id);
            item.put("descricao", descricao);
            item.put("vencimento", vencimentoDate);
            item.put("valor", valor);
            item.put("status", status);
            item.put("categoria_id", categoria_Id);
            depesas.add(item);

            cursor.moveToNext();
        }

        cursor.close();
        return depesas;
    }
}
