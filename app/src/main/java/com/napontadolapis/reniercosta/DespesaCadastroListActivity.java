package com.napontadolapis.reniercosta;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DespesaCadastroListActivity extends Activity {

    private DatabaseHelper helper;
    private SimpleDateFormat dateFormat;
    private ArrayList<Map<String, Object>> despesas;
    private ListView listViewDespesasCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa_cadastro);
        helper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        CarregarListaDeDespesas();
    }

    public void onClickbtnNovaDespesaCadastro(View view){
        CadastrarDespesa();
    }

    private void CadastrarDespesa(){
        Intent intent;
        intent = new Intent(this, DespesaEdicaoActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, Constantes.RESULTADO_GRAVOU_INFORMACAO);
    }

    private void EditarDespesa(String pIdDespesa){
        Intent intent;
        intent = new Intent(this, DespesaEdicaoActivity.class);
        intent.putExtra(Constantes.DESPESA_ID, pIdDespesa);
        //startActivity(intent);
        startActivityForResult(intent, Constantes.RESULTADO_GRAVOU_INFORMACAO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            CarregarListaDeDespesas();
        }
    }

    private void CarregarListaDeDespesas() {
        String[] de = { "VisualizacaoDaDescricao","VisualizacaoDoVencimento", "VisualizacaoDoValor" };
        int[] para = { R.id.lblDescricaoDespesaCadastro, R.id.lblVencimentoDespesaCadastro, R.id.lblValorDepesaCadastro};

        SimpleAdapter adapter = new SimpleAdapter(this,
                listarDespesas(), R.layout.lista_de_despesas, de, para);

        listViewDespesasCadastro = (ListView) findViewById(R.id.listaDespesasCadastro);
        listViewDespesasCadastro.setAdapter(adapter);
        listViewDespesasCadastro.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = despesas.get(position);
                String idDespesa = (String) map.get("id");

                if (idDespesa != null)
                    EditarDespesa(idDespesa);
                else
                    CadastrarDespesa();
            }
        });

    }

    private List<Map<String, Object>> listarDespesas() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, descricao, vencimento, " +
                        "valor, status, categoria_id FROM despesas",
                null);

        cursor.moveToFirst();

        despesas = new ArrayList<Map<String, Object>>();

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
            item.put("vencimento", dateFormat.format(vencimentoDate));
            item.put("VisualizacaoDoVencimento", "Vencimento: " + dateFormat.format(vencimentoDate));
            item.put("VisualizacaoDaDescricao", descricao);
            item.put("VisualizacaoDoValor", "Valor: R" + NumberFormat.getCurrencyInstance().format(valor));
            item.put("valor", valor);
            item.put("status", status);
            item.put("categoria_id", categoria_Id);
            despesas.add(item);

            cursor.moveToNext();
        }

        cursor.close();
        return despesas;
    }
}
