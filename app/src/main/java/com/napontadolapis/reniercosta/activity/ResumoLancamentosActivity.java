package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.napontadolapis.reniercosta.R;
import com.napontadolapis.reniercosta.model.Constantes;
import com.napontadolapis.reniercosta.repository.ResumoLancamentosRepositorio;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResumoLancamentosActivity extends Activity{

    private ResumoLancamentosRepositorio resumoLancamentosRepositorio;
    private TextView campoResumoReceitas;
    private TextView campoResumoDespesas;
    private TextView campoResumoSaldo;
    private TextView campoResumoReceitasAberto;
    private TextView campoResumoDespesasAberto;
    private TextView campoResumoSaldoAberto;
    private TextView campoResumoReceitasBaixado;
    private TextView campoResumoDespesasBaixado;
    private TextView campoResumoSaldoBaixado;
    private Spinner spnDatasParaFiltrarResumos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_lancamentos);
        resumoLancamentosRepositorio = new ResumoLancamentosRepositorio(this);
        CarregarComponentesDaTela();
        try {
            carregarSpinnerDeFiltroPorData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        CarregarValores();
    }

    public void btnVoltarOnClick(View v){
        finish();
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
        spnDatasParaFiltrarResumos.setAdapter(arrayAdapter);

        spnDatasParaFiltrarResumos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CarregarValores();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDatasParaFiltrarResumos.setSelection(arrayAdapter.getPosition(format.format(new Date())));
    }

    private void CarregarValores() {
        NumberFormat localNumberFormat = NumberFormat.getInstance(new Locale("pt", "BR"));

        Date periodo = null;
        try {
            periodo = obterDataSelecionada();
        } catch (ParseException e) {
        e.printStackTrace();
        }

        double totalResumoReceitas = resumoLancamentosRepositorio.obterTotalReceitas((periodo));
        double totalcampoResumoDespesas = resumoLancamentosRepositorio.obterTotalDespesas((periodo));
        double totalcampoResumoSaldo = totalResumoReceitas - totalcampoResumoDespesas;
        double totalcampoResumoReceitasAberto = resumoLancamentosRepositorio.obterTotalReceitasPendetes((periodo));
        double totalcampoResumoDespesasAberto = resumoLancamentosRepositorio.obterTotalDespesasPendetes((periodo));
        double totalcampoResumoSaldoAberto = totalcampoResumoReceitasAberto - totalcampoResumoDespesasAberto;
        double totalcampoResumoReceitasBaixado = resumoLancamentosRepositorio.obterTotalReceitasBaixados((periodo));
        double totalcampoResumoDespesasBaixado = resumoLancamentosRepositorio.obterTotalDespesasBaixados((periodo));
        double totalcampoResumoSaldoBaixado = totalcampoResumoReceitasBaixado - totalcampoResumoDespesasBaixado;

        campoResumoReceitas.setText(localNumberFormat.format(totalResumoReceitas));
        campoResumoDespesas.setText(localNumberFormat.format(totalcampoResumoDespesas));
        campoResumoSaldo.setText(localNumberFormat.format(totalcampoResumoSaldo));
        campoResumoReceitasAberto.setText(localNumberFormat.format(totalcampoResumoReceitasAberto));
        campoResumoDespesasAberto.setText(localNumberFormat.format(totalcampoResumoDespesasAberto));
        campoResumoSaldoAberto.setText(localNumberFormat.format(totalcampoResumoSaldoAberto));
        campoResumoReceitasBaixado.setText(localNumberFormat.format(totalcampoResumoReceitasBaixado));
        campoResumoDespesasBaixado.setText(localNumberFormat.format(totalcampoResumoDespesasBaixado));
        campoResumoSaldoBaixado.setText(localNumberFormat.format(totalcampoResumoSaldoBaixado));
    }

    private Date obterDataSelecionada() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        Date dataParaFiltrar = format.parse("01/" + spnDatasParaFiltrarResumos.getSelectedItem().toString());
        return dataParaFiltrar;
    }

    private void CarregarComponentesDaTela() {
        campoResumoReceitas = (TextView) findViewById(R.id.campoResumoReceitas);
        campoResumoDespesas = (TextView) findViewById(R.id.campoResumoDespesas);
        campoResumoSaldo = (TextView) findViewById(R.id.campoResumoSaldo);
        campoResumoReceitasAberto = (TextView) findViewById(R.id.campoResumoReceitasAberto);
        campoResumoDespesasAberto = (TextView) findViewById(R.id.campoResumoDespesasAberto);
        campoResumoSaldoAberto = (TextView) findViewById(R.id.campoResumoSaldoAberto);
        campoResumoReceitasBaixado = (TextView) findViewById(R.id.campoResumoReceitasBaixado);
        campoResumoDespesasBaixado = (TextView) findViewById(R.id.campoResumoDespesasBaixado);
        campoResumoSaldoBaixado = (TextView) findViewById(R.id.campoResumoSaldoBaixado);
        spnDatasParaFiltrarResumos = (Spinner) findViewById(R.id.spnDatasParaFiltrarResumos);
    }
}
