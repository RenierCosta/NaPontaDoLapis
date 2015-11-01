package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.napontadolapis.reniercosta.R;
import com.napontadolapis.reniercosta.dao.DatabaseHelper;
import com.napontadolapis.reniercosta.dao.ReceitaDAO;
import com.napontadolapis.reniercosta.model.Constantes;
import com.napontadolapis.reniercosta.model.Receita;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceitaCadastroActivity extends Activity{

    private SimpleDateFormat dateFormat;
    private List<Map<String, Object>> receitas;
    private ListView listViewReceitasCadastro;
    private ReceitaDAO receitaDAO;
    private Spinner spnDatasParaFiltrarReceitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita_cadastro);
        spnDatasParaFiltrarReceitas = (Spinner) findViewById(R.id.spnDatasParaFiltrarReceitaCadastro);
        try {
            carregarSpinnerDeFiltroPorData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        receitaDAO = new ReceitaDAO(this);
        dateFormat = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        try {
            carregarListaDeReceitas(obterDataSelecionada());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void carregarListaDeReceitas(Date dataParaFiltrar) {
        String[] de = { "VisualizacaoDaDescricao","VisualizacaoDoVencimento", "VisualizacaoDoValor" };
        int[] para = { R.id.lblDescricaoReceitaCadastro, R.id.lblRecebimentoReceitaCadastro, R.id.lblValorReceitaCadastro};

        SimpleAdapter adapter = new SimpleAdapter(this,
                listarReceitas(dataParaFiltrar), R.layout.lista_de_receitas, de, para);

        listViewReceitasCadastro = (ListView) findViewById(R.id.listaReceitaCadastro);
        listViewReceitasCadastro.setAdapter(adapter);
        listViewReceitasCadastro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = receitas.get(position);
                String idReceita = (String) map.get(DatabaseHelper.Receita._ID);

                if (idReceita != null)
                    EditarReceita(idReceita);
                else
                    CadastrarReceita();
            }
        });

    }

    public void onClickbtnNovaReceitaCadastro(View view){
        CadastrarReceita();
    }

    private void CadastrarReceita() {
        Intent intent;
        intent = new Intent(this, ReceitaEdicaoActivity.class);
        startActivityForResult(intent, Constantes.RESULTADO_GRAVOU_INFORMACAO);
    }

    private void EditarReceita(String idReceita) {
        Intent intent;
        intent = new Intent(this, ReceitaEdicaoActivity.class);
        intent.putExtra(Constantes.RECEITA_ID, idReceita);
        startActivityForResult(intent, Constantes.RESULTADO_GRAVOU_INFORMACAO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            try {
                carregarListaDeReceitas(obterDataSelecionada());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Map<String, Object>> listarReceitas(Date dataParaFiltrar) {
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

        List<Receita> listaDeReceitas = receitaDAO.listarTodosPorFiltro(camposParaFiltro,valoresDosCamposParaFiltro);

        receitas = new ArrayList<Map<String, Object>>();

        for (Receita receita : listaDeReceitas){
            Map<String, Object> item = new HashMap<String, Object>();

            String id = receita.getId().toString();
            String descricao = receita.getDescricao();
            Date recebimento = new Date(receita.getRecebimento().getTime());
            Date dataReceita = new Date(receita.getData().getTime());
            String status = receita.getStatus();
            long categoria_Id = receita.getCategoria().getId();
            double valor = receita.getValor();

            item.put(DatabaseHelper.Receita._ID, id);
            item.put(DatabaseHelper.Receita.DESCRICAO, descricao);
            item.put(DatabaseHelper.Receita.RECEBIMENTO, dateFormat.format(recebimento));
            item.put("VisualizacaoDoVencimento", "Recebimento: " + dateFormat.format(recebimento));
            item.put("VisualizacaoDaDescricao", descricao);
            item.put("VisualizacaoDoValor", "Valor: " + NumberFormat.getCurrencyInstance().format(valor));
            item.put(DatabaseHelper.Receita.VALOR, valor);
            item.put(DatabaseHelper.Receita.STATUS, status);
            item.put(DatabaseHelper.Receita.CATEGORIA_ID, categoria_Id);
            item.put(DatabaseHelper.Receita.DATA, dataReceita);
            receitas.add(item);
        }
        return receitas;
    }

    private Date obterDataSelecionada() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        Date dataParaFiltrar = format.parse("01/" + spnDatasParaFiltrarReceitas.getSelectedItem().toString());
        return dataParaFiltrar;
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
        spnDatasParaFiltrarReceitas.setAdapter(arrayAdapter);

        spnDatasParaFiltrarReceitas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    carregarListaDeReceitas(obterDataSelecionada());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDatasParaFiltrarReceitas.setSelection(arrayAdapter.getPosition(format.format(new Date())));
    }
}
