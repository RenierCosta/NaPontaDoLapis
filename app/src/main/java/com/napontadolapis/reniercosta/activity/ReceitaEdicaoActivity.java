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
import com.napontadolapis.reniercosta.dao.ReceitaDAO;
import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Constantes;
import com.napontadolapis.reniercosta.model.Receita;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ReceitaEdicaoActivity extends Activity{

    private EditText edtDescricao;
    private EditText edtValor;
    private Spinner spnCategoria;
    private Spinner spnStatus;
    private String idReceita;
    private SimpleDateFormat dateFormat;
    private int ano, mes, dia;
    private Button btndataRecebimento;
    private Button btnDataReceita;
    private Date dataRecebimento;
    private Date dataReceita;
    private Button btnApagarReceita;
    private ReceitaDAO receitaDAO;
    private CategoriaDAO categoriaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita_edicao);

        receitaDAO = new ReceitaDAO(this);
        categoriaDAO = new CategoriaDAO(this);

        //sempre inicializo assim
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_receita_edicao);
        carregarComponentesDaTela();
        carregarSpinnerCategoria();
        carregarSpinnerStatus();

        idReceita = getIntent().getStringExtra(Constantes.RECEITA_ID);

        if (idReceita != null) {
            CarregarReceitaAtual();
        }else{
            btnApagarReceita.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        receitaDAO.close();
        categoriaDAO.close();
        super.onDestroy();
    }

    private void CarregarReceitaAtual() {
        Receita receita = receitaDAO.buscarReceitaPorId(Long.valueOf(idReceita));

        edtDescricao.setText(receita.getDescricao());
        dataRecebimento = receita.getRecebimento();
        dataReceita = receita.getData();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        btndataRecebimento.setText(dateFormat.format(dataRecebimento));
        btnDataReceita.setText(dateFormat.format(dataReceita));
        edtValor.setText(String.valueOf(receita.getValor()));
        Categoria categoria = receita.getCategoria();

        spnCategoria.setSelection(obterPosicaoDaCategoriaNoSpinner(categoria));
        spnStatus.setSelection(obterPosicaoDoStatusNoSpinner(receita.getStatus()));
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
    protected Dialog onCreateDialog(int id) {
        if (R.id.btnRecebimentoReceita == id) {
            return new DatePickerDialog(this, dataRecebimentolistener, ano, mes, dia);
        }else if(R.id.btnDataReceita == id){
            return new DatePickerDialog(this, dataReceitalistener, ano, mes, dia);
        }

        return null;
    }
    
    private DatePickerDialog.OnDateSetListener dataRecebimentolistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataRecebimento = criarData(ano, mes, dia);
            btndataRecebimento.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    private DatePickerDialog.OnDateSetListener dataReceitalistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataReceita = criarData(ano, mes, dia);
            btnDataReceita.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        return calendar.getTime();
    }

    private void carregarSpinnerStatus() {
        List<String> status = new ArrayList<>();
        status.add(Constantes.STATUS_PENDENTE);
        status.add(Constantes.STATUS_RECEBIDO);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                status);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(arrayAdapter);
    }

    private void carregarSpinnerCategoria() {
        List<Categoria> categorias = categoriaDAO.listarTodosPorFiltro("tipo = ?",
                new String [] {String.valueOf(Constantes.ID_TIPO_CATEGORIA_RECEITA)});
        ArrayAdapter<Categoria> arrayAdapter = new ArrayAdapter<Categoria>(this,
                android.R.layout.simple_spinner_item, categorias);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategoria.setAdapter(arrayAdapter);
    }

    private void carregarComponentesDaTela() {
        edtDescricao = (EditText) findViewById(R.id.edtDescricaoReceita);
        btnApagarReceita = (Button) findViewById(R.id.btnApagarReceita);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        btndataRecebimento = (Button) findViewById(R.id.btnRecebimentoReceita);
        btnDataReceita = (Button) findViewById(R.id.btnDataReceita);
        btndataRecebimento.setText(dia + "/" + (mes + 1) + "/" + ano);
        btnDataReceita.setText(dia + "/" + (mes + 1) + "/" + ano);

        edtValor = (EditText) findViewById(R.id.edtValorReceita);
        spnCategoria = (Spinner) findViewById(R.id.spnCategoriaReceita);
        spnStatus = (Spinner) findViewById(R.id.spnStatusReceita);
    }

    public void onClickApagarReceita(View v){
        apagarDespesa(idReceita);
        finish();
    }

    private void apagarDespesa(String idReceita) {
        boolean resultado = receitaDAO.remover(Long.valueOf(idReceita));

        if (resultado){
            Toast.makeText(this, getString(R.string.registro_apagado),
                    Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        }else{
            Toast.makeText(this, getString(R.string.erro_apagado),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickCancelarReceita(View v){
        finish();
    }

    public void btnGravarReceitaOnClik(View v) throws ParseException {
        if (validarInformacoes()) {
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

        Date dataReceita = null, recebimentoReceita = null;
        SimpleDateFormat formatacaoDeData = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);

        try {
            dataReceita = formatacaoDeData.parse(btnDataReceita.getText().toString());
            recebimentoReceita = formatacaoDeData.parse(btndataRecebimento.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (recebimentoReceita.before(dataReceita)){
            Toast.makeText(this, "Data de recebimento deverá ser maior ou igual que a data da receita",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void onClickSelecionarData(View view) {
        showDialog(view.getId());
    }

    private void salvarAlteracoes() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_TELA);
        Date recebimento = sdf.parse(btndataRecebimento.getText().toString());
        Date dataDespesa = sdf.parse(btnDataReceita.getText().toString());

        Receita receita = new Receita();
        Categoria categoria = obterCategoriaSelecionada();

        receita.setDescricao(edtDescricao.getText().toString());
        receita.setRecebimento(recebimento);
        receita.setValor(Double.valueOf(edtValor.getText().toString()));
        receita.setStatus(obterStatusSelecionado());
        receita.setCategoria(categoria);
        receita.setData(dataDespesa);

        boolean resultado;

        if(idReceita == null) {
            resultado = receitaDAO.inserir(receita);
        }
        else {
            receita.setId(Long.valueOf(idReceita));
            resultado = receitaDAO.atualizar(receita);
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
