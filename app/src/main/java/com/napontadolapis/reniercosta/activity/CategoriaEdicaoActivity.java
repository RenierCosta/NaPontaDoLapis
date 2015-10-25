package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.napontadolapis.reniercosta.R;
import com.napontadolapis.reniercosta.dao.CategoriaDAO;
import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Constantes;

public class CategoriaEdicaoActivity extends Activity {

    private EditText edtDescricao;
    private RadioButton rdbTipoDespesa;
    private RadioButton rdbTipoReceita;
    private String idCategoria;
    private Button btnApagarCategoria;
    private CategoriaDAO categoriaDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriaDAO = new CategoriaDAO(this);
        //sempre inicializo assim
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_categoria_edicao);
        carregarComponentesDaTela();
        idCategoria = getIntent().getStringExtra(Constantes.CATEGORIA_ID);

        if (idCategoria != null) {
            carregarDespesaAtual();
        }else
            btnApagarCategoria.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        categoriaDAO.close();
        super.onDestroy();
    }

    private void carregarComponentesDaTela() {
        edtDescricao = (EditText) findViewById(R.id.edtDescricaoCategoria);
        rdbTipoDespesa = (RadioButton) findViewById(R.id.rdbTipoDespesaCategoria);
        rdbTipoReceita = (RadioButton) findViewById(R.id.rdbTipoReceitaCategoria);
        btnApagarCategoria = (Button) findViewById(R.id.btnApagarCategoria);
    }

    private void carregarDespesaAtual() {
        Categoria categoria = categoriaDAO.buscarCategoriaPorId(Long.valueOf(idCategoria));

        edtDescricao.setText(categoria.getDescricao());

        if (categoria.getTipo() == Constantes.ID_TIPO_CATEGORIA_DESPESA){
            rdbTipoDespesa.setChecked(true);
        }else{
            rdbTipoReceita.setChecked(true);
        }
    }
}
