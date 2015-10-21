package com.napontadolapis.reniercosta;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DespesaEdicaoActivity extends Activity {

    private DatabaseHelper helper;
    private EditText edtDescricao;
    private EditText edtDataVencimento;
    private EditText edtValor;
    private Spinner spnCategoria;
    private Spinner spnStatus;
    private String idDespesa;

    private void CarregarComponentesDaTela() {
        edtDescricao = (EditText) findViewById(R.id.edtDescricaoDespesa);
        edtDataVencimento = (EditText) findViewById(R.id.edtVencimentoDespesa);
        edtValor = (EditText) findViewById(R.id.edtValorDespesa);
        spnCategoria = (Spinner) findViewById(R.id.spnCategoriaDespesa);
        spnStatus = (Spinner) findViewById(R.id.spnStatusDespesa);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        edtDescricao.setText(cursor.getString(cursor.getColumnIndex("descricao")));
        Date vencimento = new Date(cursor.getLong(cursor.getColumnIndex("vencimento")));
        edtDataVencimento.setText(dateFormat.format(vencimento));
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
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        ContentValues values = new ContentValues();
        values.put("descricao", edtDescricao.getText().toString());
        values.put("vencimento", String.valueOf(format.parse(edtDataVencimento.getText().toString())));
        values.put("valor", edtValor.getText().toString());
        values.put("status", "Pendente");
        values.put("categoria_id", 1);


        long resultado = db.insert("despesas", null, values);

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
