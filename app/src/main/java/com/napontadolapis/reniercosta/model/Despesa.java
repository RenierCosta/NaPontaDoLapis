package com.napontadolapis.reniercosta.model;

import java.util.Date;

public class Despesa {
    Long id;
    String descricao;
    Date vencimento;
    Date data;

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    Double valor;
    String status;
    Categoria categoria;

    public Despesa(){};

    public Despesa(Long id, String descricao, Date vencimento, Double valor, String status,
                   Categoria categoria, Date data){

        this.id = id;
        this.descricao = descricao;
        this.vencimento = vencimento;
        this.valor = valor;
        this.status = status;
        this.categoria = categoria;
        this.data = data;
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

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
