package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.napontadolapis.reniercosta.R;
import com.napontadolapis.reniercosta.dao.CategoriaDAO;
import com.napontadolapis.reniercosta.dao.DespesaDAO;
import com.napontadolapis.reniercosta.dao.ReceitaDAO;
import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Constantes;

import java.text.ParseException;

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
        }else {
            btnApagarCategoria.setEnabled(false);
            rdbTipoDespesa.setChecked(true);
        }
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

    public void onClickCancelarCategoria(View view){
        finish();
    }

    public void onClickApagarCategoria(View view){
        if(validarAntesDeApagar()) {
            apagarDespesa(idCategoria);
            finish();
        }
    }

    private void apagarDespesa(String idDespesa) {
        boolean resultado = categoriaDAO.remover(Long.valueOf(idDespesa));

        if (resultado){
            Toast.makeText(this, getString(R.string.registro_apagado),
                    Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        }else{
            Toast.makeText(this, getString(R.string.erro_apagado),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarAntesDeApagar() {
        DespesaDAO despesaDAO = new DespesaDAO(this);
        ReceitaDAO receitaDAO = new ReceitaDAO(this);

        if (despesaDAO.listarTodosPorFiltro("categoria_id = ?", new String[]{idCategoria}).size() > 0){
            edtDescricao.setError("Categoria pertence a uma despesa");
            edtDescricao.setFocusable(true);
            edtDescricao.requestFocus();
            return false;
        }else
        if (receitaDAO.listarTodosPorFiltro("categoria_id = ?", new String[]{idCategoria}).size() > 0){
            edtDescricao.setError("Categoria pertence a uma receita");
            edtDescricao.setFocusable(true);
            edtDescricao.requestFocus();
            return false;
        }

        return true;
    }

    public void btnGravarDespesaOnClik(View view) throws ParseException {
        if (validarInformacoes()) {
            salvarAlteracoes();
            this.finish();
        }
    }

    private boolean validarInformacoes(){
        if (edtDescricao.getText().toString().equals("")){
            edtDescricao.setError("Informe a descrição");
            edtDescricao.setFocusable(true);
            edtDescricao.requestFocus();
            return false;
        }

        return true;
    }

    private void salvarAlteracoes() throws ParseException {
        Categoria categoria = new Categoria();
        categoria.setDescricao(edtDescricao.getText().toString());

        if (rdbTipoReceita.isChecked()){
            categoria.setTipo(Constantes.ID_TIPO_CATEGORIA_RECEITA);
        }else{
            categoria.setTipo(Constantes.ID_TIPO_CATEGORIA_DESPESA);
        }


        boolean resultado;

        if(idCategoria == null) {
            resultado = categoriaDAO.inserir(categoria);
        }
        else {
            categoria.setId(Long.valueOf(idCategoria));
            resultado = categoriaDAO.atualizar(categoria);
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

    public void onClickRadioButton(View v){
        boolean checked = ((RadioButton)v).isChecked();

        switch (v.getId()){
            case R.id.rdbTipoDespesaCategoria:
                if(checked){
                    rdbTipoReceita.setChecked(false);
                }

                break;
            case R.id.rdbTipoReceitaCategoria:
                if(checked){
                    rdbTipoDespesa.setChecked(false);
                }
                break;

        }
    }
}
