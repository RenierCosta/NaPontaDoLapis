package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.napontadolapis.reniercosta.R;
import com.napontadolapis.reniercosta.dao.CategoriaDAO;
import com.napontadolapis.reniercosta.dao.DatabaseHelper;
import com.napontadolapis.reniercosta.dao.ReceitaDAO;
import com.napontadolapis.reniercosta.model.Categoria;
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
    private Spinner spnCategoriasParaFiltrarReceitas;
    private RadioButton rdbStatusTodosReceitas;
    private RadioButton rdbStatusPendentesReceitas;
    private RadioButton rdbStatusRecebidosReceitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita_cadastro);
        carregarComponentesDaTela();
        carregarSpinnerDeFiltroPorCategoria();
        rdbStatusTodosReceitas.setChecked(true);

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

    private void carregarSpinnerDeFiltroPorCategoria() {
        CategoriaDAO categoriaDAO = new CategoriaDAO(this);

        List<Categoria> categorias = categoriaDAO.listarTodosPorFiltro("tipo = ?",
                new String [] {String.valueOf(Constantes.ID_TIPO_CATEGORIA_RECEITA)});

        Categoria categoriaVazia = new Categoria();
        categoriaVazia.setDescricao("Todos");
        categorias.add(0, categoriaVazia);
        ArrayAdapter<Categoria> arrayAdapter = new ArrayAdapter<Categoria>(this,
                android.R.layout.simple_spinner_item, categorias);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategoriasParaFiltrarReceitas.setAdapter(arrayAdapter);
        spnCategoriasParaFiltrarReceitas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }

    private void carregarComponentesDaTela() {
        spnDatasParaFiltrarReceitas = (Spinner) findViewById(R.id.spnDatasParaFiltrarReceitaCadastro);
        spnCategoriasParaFiltrarReceitas = (Spinner) findViewById(R.id.spnCategoriasParaFiltrarReceitaCadastro);
        rdbStatusTodosReceitas = (RadioButton) findViewById(R.id.rdbTodosStatusReceitas);
        rdbStatusPendentesReceitas = (RadioButton) findViewById(R.id.rdbStatusPendentesReceitas);
        rdbStatusRecebidosReceitas = (RadioButton) findViewById(R.id.rdbStatusRecebidosReceitas);
    }

    private void carregarListaDeReceitas(Date dataParaFiltrar) {
        String[] de = { "VisualizacaoDaDescricao","VisualizacaoDoVencimento", "VisualizacaoDoValor",
                "VisualizacaoDaCategoria", "VisualizacaoDoStatus"};

        int[] para = { R.id.lblDescricaoReceitaCadastro, R.id.lblRecebimentoReceitaCadastro,
                R.id.lblValorReceitaCadastro,  R.id.lblCategoriaReceitaCadastro,
                R.id.lblStatusReceitaCadastro};

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

        if (spnCategoriasParaFiltrarReceitas.getSelectedItemPosition() > 0)
            camposParaFiltro = camposParaFiltro + " AND categoria_id = ?";

        if (!rdbStatusTodosReceitas.isChecked()){
            camposParaFiltro += " AND status = ?";
        }

        calendar.setTime(dataParaFiltrar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        Date primeiroDiaDoMes = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date ultimoDiaDoMes = calendar.getTime();


        List<String> listaDosCamposParaFiltro = new ArrayList<>();
        listaDosCamposParaFiltro.add(format.format(primeiroDiaDoMes));
        listaDosCamposParaFiltro.add(format.format(ultimoDiaDoMes));

        if (spnCategoriasParaFiltrarReceitas.getSelectedItemPosition() > 0)
            listaDosCamposParaFiltro.add(((Categoria)spnCategoriasParaFiltrarReceitas.getSelectedItem()).getId().toString());

        if (!rdbStatusTodosReceitas.isChecked()){
            if (rdbStatusRecebidosReceitas.isChecked()){
                listaDosCamposParaFiltro.add(Constantes.STATUS_RECEBIDO);
            }else if (rdbStatusPendentesReceitas.isChecked()){
                listaDosCamposParaFiltro.add(Constantes.STATUS_PENDENTE);
            }
        }

        String [] valoresDosCamposParaFiltro = new String[listaDosCamposParaFiltro.size()];
        valoresDosCamposParaFiltro = listaDosCamposParaFiltro.toArray(valoresDosCamposParaFiltro);

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
            item.put("VisualizacaoDaCategoria", "Categoria: " + receita.getCategoria().getDescricao());
            item.put("VisualizacaoDoStatus", "Status: " + receita.getStatus());

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

        dataInicial = format.parse("01/01/2016");

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

    public void onClickRadioButtonCategoriasReceitas(View v){
        boolean checked = ((RadioButton)v).isChecked();

        switch (v.getId()){
            case R.id.rdbTodosStatusReceitas:
                if(checked){
                    rdbStatusPendentesReceitas.setChecked(false);
                    rdbStatusRecebidosReceitas.setChecked(false);
                }

                break;
            case R.id.rdbStatusPendentesReceitas:
                if(checked){
                    rdbStatusTodosReceitas.setChecked(false);
                    rdbStatusRecebidosReceitas.setChecked(false);
                }
                break;
            case R.id.rdbStatusRecebidosReceitas:
                if(checked){
                    rdbStatusTodosReceitas.setChecked(false);
                    rdbStatusPendentesReceitas.setChecked(false);
                }
                break;
        }

        try {
            carregarListaDeReceitas(obterDataSelecionada());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
