package com.napontadolapis.reniercosta;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class DespesaCadastroListActivity extends ListActivity implements OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listarDespesas()));
        ListView listView = getListView();
        listView.setOnItemClickListener(this);
    }

    private List<String> listarDespesas() {
        return Arrays.asList("Carro", "Cartão", "Água");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view;
        String mensagem = "Despesa selecionada: " + textView.getText();
        Toast.makeText(getApplicationContext(), mensagem,
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DespesaEdicaoActivity.class));
    }
}
