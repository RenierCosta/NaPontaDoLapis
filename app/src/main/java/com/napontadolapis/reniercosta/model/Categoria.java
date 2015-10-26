package com.napontadolapis.reniercosta.model;

public class Categoria {
    Long id;
    String descricao;
    int tipo;

    public Categoria(){}

    public Categoria(Long id, String descricao, int tipo){
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
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

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return this.getDescricao();
    }
}
