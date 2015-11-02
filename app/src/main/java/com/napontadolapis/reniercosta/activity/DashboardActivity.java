package com.napontadolapis.reniercosta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.napontadolapis.reniercosta.R;


public class DashboardActivity extends Activity {

    public void despesasOnClick(View v){
        startActivity(new Intent(DashboardActivity.this, DespesaCadastroActivity.class));
    }

    public void receitasOnClick(View v){
        startActivity(new Intent(DashboardActivity.this, ReceitaCadastroActivity.class));
    }

    public void categoriasOnClick(View v){
        startActivity(new Intent(DashboardActivity.this, CategoriaCadastroActivity.class));
    }

    public void resumosOnClick(View v){
        startActivity(new Intent(DashboardActivity.this, ResumoLancamentosActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
