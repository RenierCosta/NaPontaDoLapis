package com.napontadolapis.reniercosta;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.napontadolapis.reniercosta.dao.CategoriaDAO;
import com.napontadolapis.reniercosta.dao.DespesaDAO;
import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Despesa;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DespesaEdicaoActivity extends Activity {

    private EditText edtDescricao;
    private EditText edtValor;
    private Spinner spnCategoria;
    private Spinner spnStatus;
    private String idDespesa;
    private SimpleDateFormat dateFormat;
    private int ano, mes, dia;
    private Button btndataVencimento;
    private Date dataVencimento;
    private Button btnApagarDespesa;
    private DespesaDAO despesaDAO;

    private void CarregarComponentesDaTela() {
        edtDescricao = (EditText) findViewById(R.id.edtDescricaoDespesa);
        btnApagarDespesa = (Button) findViewById(R.id.btnApagarDespesa);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        btndataVencimento = (Button) findViewById(R.id.btnVencimentoDespesa);
        btndataVencimento.setText(dia + "/" + (mes + 1) + "/" + ano);

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
            return new DatePickerDialog(this, listener, ano, mes, dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataVencimento = criarData(ano, mes, dia);
            btndataVencimento.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        return calendar.getTime();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DespesaDAO despesaDAO = new DespesaDAO(this);

        //sempre inicializo assim
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_despesa_edicao);
        CarregarComponentesDaTela();

        idDespesa = getIntent().getStringExtra(Constantes.DESPESA_ID);

        if (idDespesa != null) {
            CarregarDespesaAtual();
        }

    }

    private void CarregarDespesaAtual() {
        Despesa despesa = despesaDAO.buscarDespesaPorId(Long.valueOf(idDespesa));

        edtDescricao.setText(despesa.getDescricao());
        dataVencimento = despesa.getVencimento();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        btndataVencimento.setText(dateFormat.format(dataVencimento));
        edtValor.setText(String.valueOf(despesa.getValor()));
    }

    @Override
    protected void onDestroy() {
        despesaDAO.close();
        super.onDestroy();
    }

    public void btnGravarDespesaOnClik(View view) throws ParseException {
        salvarAlteracoes();
        this.finish();
    }

    private void salvarAlteracoes() throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date vencimento = sdf.parse(btndataVencimento.getText().toString());

        Despesa despesa = new Despesa();
        Categoria categoria = obterCategoriaSelecionada();

        despesa.setDescricao(edtDescricao.getText().toString());
        despesa.setVencimento(vencimento);
        despesa.setValor(Double.valueOf(edtValor.getText().toString()));
        despesa.setStatus("Pendente");
        despesa.setCategoria(categoria);

        boolean resultado;

        if(idDespesa == null) {
            resultado = despesaDAO.inserir(despesa);
        }
        else {
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

    private Categoria obterCategoriaSelecionada() {
        CategoriaDAO categoriaDAO = new CategoriaDAO(this);
        return categoriaDAO.buscarCategoriaPorId(Long.valueOf(1));
    }


}
