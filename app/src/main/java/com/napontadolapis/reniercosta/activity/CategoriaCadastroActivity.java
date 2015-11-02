package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.napontadolapis.reniercosta.R;
import com.napontadolapis.reniercosta.dao.CategoriaDAO;
import com.napontadolapis.reniercosta.dao.DatabaseHelper;
import com.napontadolapis.reniercosta.model.Categoria;
import com.napontadolapis.reniercosta.model.Constantes;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriaCadastroActivity extends Activity{

    private List<Map<String, Object>> categorias;
    private ListView listViewCategoriasCadastro;
    private CategoriaDAO categoriasDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_cadastro);
        categoriasDAO = new CategoriaDAO(this);
        CarregarListaDeCategorias();
    }

    private void CarregarListaDeCategorias() {
        String[] de = { "VisualizacaoDaDescricao","VisualizacaoDoTipo" };
        int[] para = { R.id.lblDescricaoCategoriaCadastro, R.id.lblTipoCategoriaCadastro};

        SimpleAdapter adapter = new SimpleAdapter(this,
                listarCategorias(), R.layout.lista_de_categorias, de, para);

        listViewCategoriasCadastro = (ListView) findViewById(R.id.listaCategoriaCadastro);
        listViewCategoriasCadastro.setAdapter(adapter);
        listViewCategoriasCadastro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = categorias.get(position);
                String idCategoria = (String) map.get(DatabaseHelper.Categoria._ID);

                if (idCategoria != null)
                    EditarCategoria(idCategoria);
                else
                    CadastrarCategoria();
            }
        });
    }

    public void onClickbtnNovaCategoriaCadastro(View view){
        CadastrarCategoria();
    }

    private void EditarCategoria(String idCategoria){
        Intent intent;
        intent = new Intent(this, CategoriaEdicaoActivity.class);
        intent.putExtra(Constantes.CATEGORIA_ID, idCategoria);
        startActivityForResult(intent, Constantes.RESULTADO_GRAVOU_INFORMACAO);
    }

    private void CadastrarCategoria(){
        Intent intent;
        intent = new Intent(this, CategoriaEdicaoActivity.class);
        startActivityForResult(intent, Constantes.RESULTADO_GRAVOU_INFORMACAO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            CarregarListaDeCategorias();
        }
    }

    private List<Map<String, Object>> listarCategorias() {
        List<Categoria> listaDeCategorias = categoriasDAO.listarTodos();
        categorias = new ArrayList<Map<String, Object>>();

        for (Categoria categoria : listaDeCategorias){
            Map<String, Object> item = new HashMap<String, Object>();

            String id = categoria.getId().toString();
            String descricao = categoria.getDescricao();
            int tipo = categoria.getTipo();

            String descricaoTipoCategoria;

            if (tipo == Constantes.ID_TIPO_CATEGORIA_DESPESA){
                descricaoTipoCategoria = Constantes.DESCRICAO_TIPO_CATEGORIA_DESPESA;
            }else{
                descricaoTipoCategoria = Constantes.DESCRICAO_TIPO_CATEGORIA_RECEITA;
            }

            item.put(DatabaseHelper.Categoria._ID, id);
            item.put(DatabaseHelper.Categoria.DESCRICAO, descricao);
            item.put(DatabaseHelper.Categoria.TIPO, tipo);
            item.put("VisualizacaoDaDescricao", descricao);
            item.put("VisualizacaoDoTipo", "Categoria: " + descricaoTipoCategoria);

            categorias.add(item);
        }
        return categorias;
    }
}
