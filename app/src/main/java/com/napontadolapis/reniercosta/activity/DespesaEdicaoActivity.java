package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.napontadolapis.reniercosta.R;
import com.napontadolapis.reniercosta.dao.CategoriaDAO;
import com.napontadolapis.reniercosta.dao.DespesaDAO;
import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Constantes;
import com.napontadolapis.reniercosta.model.Despesa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DespesaEdicaoActivity extends Activity {

    private EditText edtDescricao;
    private EditText edtValor;
    private Spinner spnCategoria;
    private Spinner spnStatus;
    private String idDespesa;
    private int ano, mes, dia;
    private Button btndataVencimento;
    private Button btndataDataDespesa;
    private Date dataVencimento;
    private Date dataDataDespesa;
    private Button btnApagarDespesa;
    private DespesaDAO despesaDAO;
    private CategoriaDAO categoriaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        despesaDAO = new DespesaDAO(this);
        categoriaDAO = new CategoriaDAO(this);


        //sempre inicializo assim
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_despesa_edicao);
        carregarComponentesDaTela();
        carregarSpinnerCategoria();
        carregarSpinnerStatus();

        idDespesa = getIntent().getStringExtra(Constantes.DESPESA_ID);

        if (idDespesa != null) {
            CarregarDespesaAtual();
        }else{
            btnApagarDespesa.setEnabled(false);
        }

    }

    private void carregarSpinnerStatus() {
        List<String> status = new ArrayList<>();
        status.add(Constantes.STATUS_PENDENTE);
        status.add(Constantes.STATUS_PAGO);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                status);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(arrayAdapter);
    }

    private void carregarSpinnerCategoria() {
        List<Categoria> categorias = categoriaDAO.listarTodosPorFiltro("tipo = ?",
                new String [] {String.valueOf(Constantes.ID_TIPO_CATEGORIA_DESPESA)});
        ArrayAdapter<Categoria> arrayAdapter = new ArrayAdapter<Categoria>(this,
                android.R.layout.simple_spinner_item, categorias);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategoria.setAdapter(arrayAdapter);
    }

    private void carregarComponentesDaTela() {
        edtDescricao = (EditText) findViewById(R.id.edtDescricaoDespesa);
        btnApagarDespesa = (Button) findViewById(R.id.btnApagarDespesa);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        btndataVencimento = (Button) findViewById(R.id.btnVencimentoDespesa);
        btndataDataDespesa = (Button) findViewById(R.id.btnDataDespesa);
        btndataVencimento.setText(dia + "/" + (mes + 1) + "/" + ano);
        btndataDataDespesa.setText(dia + "/" + (mes + 1) + "/" + ano);
        dataDataDespesa = criarData(ano,mes,dia);
        dataVencimento = criarData(ano, mes, dia);


        edtValor = (EditText) findViewById(R.id.edtValorDespesa);
        spnCategoria = (Spinner) findViewById(R.id.spnCategoriaDespesa);
        spnStatus = (Spinner) findViewById(R.id.spnStatusDespesa);
    }

    public void onClickVencimentoDespesa(View view) {
        showDialog(view.getId());
    }

    public void onClickCancelarDespesa(View view){
        finish();
    }

    public void onClickApagarDespesa(View view){
        apagarDespesa(idDespesa);
        finish();
    }

    private void apagarDespesa(String idDespesa) {
        boolean resultado = despesaDAO.remover(Long.valueOf(idDespesa));

        if (resultado){
            Toast.makeText(this, getString(R.string.registro_apagado),
                    Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        }else{
            Toast.makeText(this, getString(R.string.erro_apagado),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (R.id.btnVencimentoDespesa == id) {
            return new DatePickerDialog(this, dataVencimentolistener, ano, mes, dia);
        }else if(R.id.btnDataDespesa == id){
            return new DatePickerDialog(this, dataDataDespesalistener, ano, mes, dia);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener dataVencimentolistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataVencimento = criarData(ano, mes, dia);
            btndataVencimento.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    private DatePickerDialog.OnDateSetListener dataDataDespesalistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataDataDespesa = criarData(ano, mes, dia);
            btndataDataDespesa.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        return calendar.getTime();
    }

    private void CarregarDespesaAtual() {
        Despesa despesa = despesaDAO.buscarDespesaPorId(Long.valueOf(idDespesa));

        edtDescricao.setText(despesa.getDescricao());
        dataVencimento = despesa.getVencimento();
        dataDataDespesa = despesa.getData();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        btndataVencimento.setText(dateFormat.format(dataVencimento));
        btndataDataDespesa.setText(dateFormat.format(dataDataDespesa));
        edtValor.setText(String.valueOf(despesa.getValor()));
        Categoria categoria = despesa.getCategoria();

        spnCategoria.setSelection(obterPosicaoDaCategoriaNoSpinner(categoria));
        spnStatus.setSelection(obterPosicaoDoStatusNoSpinner(despesa.getStatus()));
    }

    private int obterPosicaoDoStatusNoSpinner(String status) {
        int posicao = 0;

        for (int i=0;i<spnStatus.getCount();i++){
            if (spnStatus.getItemAtPosition(i).equals(status)){
                posicao = i;
            }
        }

        return posicao;
    }

    private int obterPosicaoDaCategoriaNoSpinner(Categoria categoria) {
        int posicao = 0;
        Categoria categoriaDoSpiiner;

        for (int i=0;i<spnCategoria.getCount();i++){
            categoriaDoSpiiner = (Categoria)spnCategoria.getItemAtPosition(i);
            if (categoria.getId().equals(categoriaDoSpiiner.getId())){
                posicao = i;
            }
        }

        return posicao;
    }

    @Override
    protected void onDestroy() {
        despesaDAO.close();
        categoriaDAO.close();
        super.onDestroy();
    }

    public void btnGravarDespesaOnClik(View view) throws ParseException {
        if (validarInformacoes()){
            salvarAlteracoes();
            this.finish();
        }
    }

    private boolean validarInformacoes() {
        if (edtDescricao.getText().toString().equals("")){
            edtDescricao.setError("Informe a descrição");
            edtDescricao.setFocusable(true);
            edtDescricao.requestFocus();
            return false;
        }

        if (edtValor.getText().toString().equals("")){
            edtValor.setError("Informe o valor");
            edtValor.setFocusable(true);
            edtValor.requestFocus();
            return false;
        }

        Date dataDespesa = null, vencimentoDespesa = null;
        SimpleDateFormat formatacaoDeData = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);

        try {
            dataDespesa = formatacaoDeData.parse(btndataDataDespesa.getText().toString());
            vencimentoDespesa = formatacaoDeData.parse(btndataVencimento.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dataVencimento.before(dataDespesa)){
            Toast.makeText(this, "Data de vencimento deverá ser maior ou igual que a data da despesa",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void salvarAlteracoes() throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        Date vencimento = sdf.parse(btndataVencimento.getText().toString());
        Date dataDespesa = sdf.parse(btndataDataDespesa.getText().toString());

        Despesa despesa = new Despesa();
        Categoria categoria = obterCategoriaSelecionada();

        despesa.setDescricao(edtDescricao.getText().toString());
        despesa.setVencimento(vencimento);
        despesa.setValor(Double.valueOf(edtValor.getText().toString()));
        despesa.setStatus(obterStatusSelecionado());
        despesa.setCategoria(categoria);
        despesa.setData(dataDespesa);

        boolean resultado;

        if(idDespesa == null) {
            resultado = despesaDAO.inserir(despesa);
        }
        else {
            despesa.setId(Long.valueOf(idDespesa));
            resultado = despesaDAO.atualizar(despesa);
        }

        if(resultado){
            Toast.makeText(this, getString(R.string.registro_salvo),
                    Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String obterStatusSelecionado() {
        return spnStatus.getSelectedItem().toString();
    }

    private Categoria obterCategoriaSelecionada() {
        return (Categoria)spnCategoria.getSelectedItem();
    }


}
