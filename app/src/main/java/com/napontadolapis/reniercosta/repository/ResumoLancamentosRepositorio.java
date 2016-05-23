package com.napontadolapis.reniercosta.repository;

import android.content.Context;

import com.napontadolapis.reniercosta.dao.DespesaDAO;
import com.napontadolapis.reniercosta.dao.ReceitaDAO;
import com.napontadolapis.reniercosta.model.Constantes;
import com.napontadolapis.reniercosta.model.Despesa;
import com.napontadolapis.reniercosta.model.Receita;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ResumoLancamentosRepositorio {

    private DespesaDAO despesaDAO;
    private ReceitaDAO receitaDAO;
    private SimpleDateFormat dateFormat;

    public ResumoLancamentosRepositorio(Context context){
        despesaDAO = new DespesaDAO(context);
        receitaDAO = new ReceitaDAO(context);
        dateFormat = new SimpleDateFormat(Constantes.MASCARA_DE_DATA_PARA_BANCO);
    }

    public double obterTotalReceitas(Date periodo){
        Date primeiroDiaDoMes = obterPrimeiroDiaDoMes(periodo);
        Date ultimoDiaDoMes = obterUltimoDiaDoMes(periodo);

        String where = "data between ? and ?";
        String [] whereArg = new String[]{dateFormat.format(primeiroDiaDoMes),
                dateFormat.format(ultimoDiaDoMes)};

        List<Receita> receitas = receitaDAO.listarTodosPorFiltro(where,whereArg);
        double totalReceitas = 0;

        for (Receita receita:receitas){
            totalReceitas = totalReceitas + receita.getValor();
        }

        return totalReceitas;
    }

    public double obterTotalDespesas(Date periodo){
        Date primeiroDiaDoMes = obterPrimeiroDiaDoMes(periodo);
        Date ultimoDiaDoMes = obterUltimoDiaDoMes(periodo);

        String where = "data between ? and ?";
        String [] whereArg = new String[]{dateFormat.format(primeiroDiaDoMes),
                dateFormat.format(ultimoDiaDoMes)};

        List<Despesa> despesas = despesaDAO.listarTodosPorFiltro(where,whereArg);
        double totalDespesas = 0;

        for (Despesa despesa:despesas){
            totalDespesas = totalDespesas + despesa.getValor();
        }

        return totalDespesas;
    }

    public double obterTotalReceitasPendetes(Date periodo){
        Date primeiroDiaDoMes = obterPrimeiroDiaDoMes(periodo);
        Date ultimoDiaDoMes = obterUltimoDiaDoMes(periodo);

        String where = "(data between ? and ?) and (status = ?)";
        String [] whereArg = new String[]{dateFormat.format(primeiroDiaDoMes),
                dateFormat.format(ultimoDiaDoMes), Constantes.STATUS_PENDENTE};

        List<Receita> receitas = receitaDAO.listarTodosPorFiltro(where,whereArg);
        double totalReceitas = 0;

        for (Receita receita:receitas){
            totalReceitas = totalReceitas + receita.getValor();
        }

        return totalReceitas;
    }

    private Date obterPrimeiroDiaDoMes(Date data){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date obterUltimoDiaDoMes(Date data){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public double obterTotalDespesasPendetes(Date periodo){
        Date primeiroDiaDoMes = obterPrimeiroDiaDoMes(periodo);
        Date ultimoDiaDoMes = obterUltimoDiaDoMes(periodo);

        String where = "(data between ? and ?) and (status = ?)";
        String [] whereArg = new String[]{dateFormat.format(primeiroDiaDoMes),
                dateFormat.format(ultimoDiaDoMes), Constantes.STATUS_PENDENTE};

        List<Despesa> despesas = despesaDAO.listarTodosPorFiltro(where,whereArg);
        double totalDespesas = 0;

        for (Despesa despesa:despesas){
            totalDespesas = totalDespesas + despesa.getValor();
        }

        return totalDespesas;
    }

    public double obterTotalReceitasBaixados(Date periodo) {
        Date primeiroDiaDoMes = obterPrimeiroDiaDoMes(periodo);
        Date ultimoDiaDoMes = obterUltimoDiaDoMes(periodo);

        String where = "(data between ? and ?) and (status <> ?)";
        String [] whereArg = new String[]{dateFormat.format(primeiroDiaDoMes),
                dateFormat.format(ultimoDiaDoMes), Constantes.STATUS_PENDENTE};

        List<Receita> receitas = receitaDAO.listarTodosPorFiltro(where,whereArg);
        double totalReceitas = 0;

        for (Receita receita:receitas){
            totalReceitas = totalReceitas + receita.getValor();
        }

        return totalReceitas;
    }

    public double obterTotalDespesasBaixados(Date periodo) {
        Date primeiroDiaDoMes = obterPrimeiroDiaDoMes(periodo);
        Date ultimoDiaDoMes = obterUltimoDiaDoMes(periodo);

        String where = "(data between ? and ?) and (status <> ?)";
        String [] whereArg = new String[]{dateFormat.format(primeiroDiaDoMes),
                dateFormat.format(ultimoDiaDoMes), Constantes.STATUS_PENDENTE};

        List<Despesa> despesas = despesaDAO.listarTodosPorFiltro(where,whereArg);
        double totalDespesas = 0;

        for (Despesa despesa:despesas){
            totalDespesas = totalDespesas + despesa.getValor();
        }

        return totalDespesas;
    }
}
