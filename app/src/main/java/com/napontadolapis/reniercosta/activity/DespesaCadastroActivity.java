package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.napontadolapis.reniercosta.R;
import com.napontadolapis.reniercosta.dao.DatabaseHelper;
import com.napontadolapis.reniercosta.dao.DespesaDAO;
import com.napontadolapis.reniercosta.model.Constantes;
import com.napontadolapis.reniercosta.model.Despesa;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DespesaCadastroActivity extends Activity {

    private SimpleDateFormat dateFormat;
    private List<Map<String, Object>>  despesas;
    private ListView listViewDespesasCadastro;
    private DespesaDAO despesaDAO;
    private Spinner spnDatasParaFiltrarDespesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa_cadastro);
        spnDatasParaFiltrarDespesas = (Spinner) findViewById(R.id.spnDatasParaFiltrarDespesas);
        try {
            carregarSpinnerDeFiltroPorData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        despesaDAO = new DespesaDAO(this);
        dateFormat = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        try {
            carregarListaDeDespesas(obterDataSelecionada());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void carregarSpinnerDeFiltroPorData() throws ParseException {
        Date dataInicial;
        Date dataAtual;
        SimpleDateFormat format = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);

        dataInicial = format.parse("01/01/2015");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataInicial);
        format.applyPattern("MM/yyyy");
        List<String> listaDeDatas = new ArrayList<>();

        dataAtual = calendar.getTime();
        listaDeDatas.add(format.format(dataAtual));

        for (int i=0;i < 11;i++){
            calendar.add(Calendar.MONTH, 1);
            dataAtual = calendar.getTime();
            listaDeDatas.add(format.format(dataAtual));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, listaDeDatas);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDatasParaFiltrarDespesas.setAdapter(arrayAdapter);

        spnDatasParaFiltrarDespesas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    carregarListaDeDespesas(obterDataSelecionada());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDatasParaFiltrarDespesas.setSelection(arrayAdapter.getPosition(format.format(new Date())));
    }

    private Date obterDataSelecionada() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        Date dataParaFiltrar = format.parse("01/" + spnDatasParaFiltrarDespesas.getSelectedItem().toString());
        return dataParaFiltrar;
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
            try {
                carregarListaDeDespesas(obterDataSelecionada());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void carregarListaDeDespesas(Date dataParaFiltrar) {
        String[] de = { "VisualizacaoDaDescricao","VisualizacaoDoVencimento", "VisualizacaoDoValor" };
        int[] para = { R.id.lblDescricaoDespesaCadastro, R.id.lblVencimentoDespesaCadastro, R.id.lblValorDepesaCadastro};

        SimpleAdapter adapter = new SimpleAdapter(this,
                listarDespesas(dataParaFiltrar), R.layout.lista_de_despesas, de, para);

        listViewDespesasCadastro = (ListView) findViewById(R.id.listaDespesasCadastro);
        listViewDespesasCadastro.setAdapter(adapter);
        listViewDespesasCadastro.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = despesas.get(position);
                String idDespesa = (String) map.get(DatabaseHelper.Despesa._ID);

                if (idDespesa != null)
                    EditarDespesa(idDespesa);
                else
                    CadastrarDespesa();
            }
        });

    }

    private List<Map<String, Object>> listarDespesas(Date dataParaFiltrar) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_BANCO);
        String camposParaFiltro = "data BETWEEN ? AND ?";

        calendar.setTime(dataParaFiltrar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        Date primeiroDiaDoMes = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date ultimoDiaDoMes = calendar.getTime();


        String [] valoresDosCamposParaFiltro = new String[]{format.format(primeiroDiaDoMes),
                format.format(ultimoDiaDoMes)};

        //List<Despesa> listaDedespesas = despesaDAO.listarTodosPorFiltro(camposParaFiltro,valoresDosCamposParaFiltro);
        List<Despesa> listaDedespesas = despesaDAO.listarTodos();
        despesas = new ArrayList<Map<String, Object>>();

        for (Despesa despesa : listaDedespesas){
            Map<String, Object> item = new HashMap<String, Object>();

            String id = despesa.getId().toString();
            String descricao = despesa.getDescricao();
            Date vencimento = new Date(despesa.getVencimento().getTime());
            Date dataDespesa = new Date(despesa.getData().getTime());
            String status = despesa.getStatus();
            long categoria_Id = despesa.getCategoria().getId();
            double valor = despesa.getValor();

            item.put(DatabaseHelper.Despesa._ID, id);
            item.put(DatabaseHelper.Despesa.DESCRICAO, descricao);
            item.put(DatabaseHelper.Despesa.VENCIMENTO, dateFormat.format(vencimento));
            item.put("VisualizacaoDoVencimento", "Vencimento: " + dateFormat.format(vencimento));
            item.put("VisualizacaoDaDescricao", descricao);
            item.put("VisualizacaoDoValor", "Valor: R" + NumberFormat.getCurrencyInstance().format(valor));
            item.put(DatabaseHelper.Despesa.VALOR, valor);
            item.put(DatabaseHelper.Despesa.STATUS, status);
            item.put(DatabaseHelper.Despesa.CATEGORIA_ID, categoria_Id);
            item.put(DatabaseHelper.Despesa.DATA, dataDespesa);
            despesas.add(item);
        }
        return despesas;
    }
}
