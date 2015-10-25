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

import com.napontadolapis.reniercosta.dao.DespesaDAO;
import com.napontadolapis.reniercosta.model.Despesa;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DespesaCadastroListActivity extends Activity {

    private SimpleDateFormat dateFormat;
    private List<Map<String, Object>>  despesas;
    private ListView listViewDespesasCadastro;
    private DespesaDAO despesaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa_cadastro);
        despesaDAO = new DespesaDAO(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        CarregarListaDeDespesas();
    }

    public void onClickbtnNovaDespesaCadastro(View view){
        CadastrarDespesa();
    }

    private void CadastrarDespesa(){
        Intent intent;
        intent = new Intent(this, DespesaEdicaoActivity.class);
        startActivityForResult(intent, Constantes.RESULTADO_GRAVOU_INFORMACAO);
    }

    private void EditarDespesa(String pIdDespesa){
        Intent intent;
        intent = new Intent(this, DespesaEdicaoActivity.class);
        intent.putExtra(Constantes.DESPESA_ID, pIdDespesa);
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
        List<Despesa> listaDedespesas = despesaDAO.listarTodos();
        despesas = new ArrayList<Map<String, Object>>();

        for (Despesa despesa : listaDedespesas){
            Map<String, Object> item = new HashMap<String, Object>();

            String id = despesa.getId().toString();
            String descricao = despesa.getDescricao();
            Date vencimento = new Date(despesa.getVencimento().getTime());
            String status = despesa.getStatus();
            long categoria_Id = despesa.getCategoria().getId();
            double valor = despesa.getValor();

            item.put("id", id);
            item.put("descricao", descricao);
            item.put("vencimento", dateFormat.format(vencimento));
            item.put("VisualizacaoDoVencimento", "Vencimento: " + dateFormat.format(vencimento));
            item.put("VisualizacaoDaDescricao", descricao);
            item.put("VisualizacaoDoValor", "Valor: R" + NumberFormat.getCurrencyInstance().format(valor));
            item.put("valor", valor);
            item.put("status", status);
            item.put("categoria_id", categoria_Id);
            despesas.add(item);
        }
        return despesas;
    }
}
