package br.com.app.financeiro.model;

import java.math.BigDecimal;

public class ConversaoMoedas {
    private Long usuarioId;
    private String nomeUsuario;
    private BigDecimal valor;
    private String moeda;
    private BigDecimal saldoConvertido;

    public ConversaoMoedas(){}

    public ConversaoMoedas(Long usuarioId , String nomeUsuario, BigDecimal valor, String moeda, BigDecimal saldoConvertido){
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.valor = valor;
        this.moeda = moeda;
        this.saldoConvertido = saldoConvertido;
    }

    public Long getUsuarioId(){
        return this.usuarioId;
    }
    public void setUsuarioId(Long usuarioId){
        this.usuarioId = usuarioId;
    }
    public String getNomeUsuario(){
        return this.nomeUsuario;
    }
    public void setNomeUsuario(String nomeUsuario){
        this.nomeUsuario = nomeUsuario;
    }
    public BigDecimal getValor(){
        return this.valor;
    }
    public void setValor(BigDecimal valor){
        this.valor = valor;
    }
    public String getMoeda(){
        return this.moeda;
    }
    public void setMoeda(String moeda){
        this.moeda = moeda;
    }
    public BigDecimal getSaldoConvertido(){
        return this.saldoConvertido;
    }
    public void setSaldoConvertido(BigDecimal saldoConvertido){
        this.saldoConvertido = saldoConvertido;
    }
}
