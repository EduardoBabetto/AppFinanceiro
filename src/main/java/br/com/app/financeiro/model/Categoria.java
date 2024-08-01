package br.com.app.financeiro.model;

import java.math.BigDecimal;

public class Categoria {

    private Long id;
    private String nome;
    private String descricao;
    private Long usuarioId;
    private BigDecimal valor;


    public Categoria() {}

    public Categoria(Long id, String nome, String descricao, Long usuarioId, BigDecimal valor) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.usuarioId = usuarioId;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {    
        this.valor = valor;
    }
    
}
