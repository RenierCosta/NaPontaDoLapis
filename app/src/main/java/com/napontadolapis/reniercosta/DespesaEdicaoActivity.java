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

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DespesaEdicaoActivity extends Activity {

    private DatabaseHelper helper;
    private EditText edtDescricao;
    private EditText edtValor;
    private Spinner spnCategoria;
    private Spinner spnStatus;
    private String idDespesa;
    private SimpleDateFormat dateFormat;
    private int ano, mes, dia;
    private Button btndataVencimento;
    private Date dataVencimento;

    private void CarregarComponentesDaTela() {
        edtDescricao = (EditText) findViewById(R.id.edtDescricaoDespesa);

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
        setContentView(R.layout.activity_despesa_edicao);
        CarregarComponentesDaTela();
        helper = new DatabaseHelper(this);

        idDespesa = getIntent().getStringExtra(Constantes.DESPESA_ID);

        if (idDespesa != null)
            CarregarDespesaAtual();

    }

    private void CarregarDespesaAtual() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor =
                db.rawQuery("SELECT descricao, vencimento, " +
                        "valor, status, categoria_id " +
                        "FROM despesas WHERE _id = ?", new String[]{ idDespesa });

        cursor.moveToFirst();

        edtDescricao.setText(cursor.getString(cursor.getColumnIndex("descricao")));
        dataVencimento = new Date(cursor.getLong(cursor.getColumnIndex("vencimento")));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        btndataVencimento.setText(dateFormat.format(dataVencimento));
        edtValor.setText(cursor.getString(cursor.getColumnIndex("valor")));

        cursor.close();
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

    public void btnGravarDespesaOnClik(View view) throws ParseException {
        SQLiteDatabase db = helper.getWritableDatabase();
        Date vencimento = new Date(btndataVencimento.getText().toString());


        ContentValues values = new ContentValues();
        values.put("descricao", edtDescricao.getText().toString());
        values.put("vencimento", dataVencimento.getTime());
        values.put("valor", edtValor.getText().toString());
        values.put("status", "Pendente");
        values.put("categoria_id", 1);

        long resultado;

        if(idDespesa == null) {
            resultado = db.insert("despesas", null, values);
        }
        else {
            resultado = db.update("despesas", values, "_id = ?", new String[]{idDespesa});
        }


        if(resultado != -1 ){
            Toast.makeText(this, getString(R.string.registro_salvo),
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar),
                    Toast.LENGTH_SHORT).show();
        }

        this.finish();
    }


}
