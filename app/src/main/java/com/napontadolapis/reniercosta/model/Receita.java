package com.napontadolapis.reniercosta.model;

import java.util.Date;

/**
 * Created by Renier on 31/10/2015.
 */
public class Receita {
    Long id;
    String descricao;
    Date data;
    Date recebimento;
    Categoria categoria;
    String status;
    Double valor;

    public Receita(){}

    public Receita(Long id, String descricao, Date data, Date recebimento, Categoria categoria,
                   String status, Double valor){

        this.id = id;
        this.descricao = descricao;
        this.data = data;
        this.recebimento = recebimento;
        this.categoria = categoria;
        this.status = status;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getRecebimento() {
        return recebimento;
    }

    public void setRecebimento(Date recebimento) {
        this.recebimento = recebimento;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
