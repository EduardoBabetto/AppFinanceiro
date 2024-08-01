package br.com.app.financeiro.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.app.financeiro.enuns.TipoTransferencia;

public class Transferencia {
    private Long id;
    private Long usuarioId;
    private String nomeDestinatario;
    private BigDecimal valor;
    private String categoria;
    private TipoTransferencia tipo;
    private LocalDate data;

    public Transferencia() {}

    public Transferencia(
        Long id, Long usuarioId, BigDecimal valor, String categoria, TipoTransferencia tipo, LocalDate data, String nomeDestinatario) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.valor = valor;
        this.categoria = categoria;
        this.tipo = tipo;
        this.data = data;
        this.nomeDestinatario = nomeDestinatario;
    }

    // getters e setters

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUsuarioId() {
        return this.usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    public BigDecimal getValor() {
        return this.valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    public String getCategoria() {
        return this.categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public TipoTransferencia getTipo() {
        return this.tipo;
    }
    public void setTipo(TipoTransferencia tipo) {
        this.tipo = tipo;
    }
    public LocalDate getData() {
        return this.data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getNomeDestinatario() {
        return this.nomeDestinatario;
    }

    public void setNomeDestinatario(String nomeDestinatario) {
        this.nomeDestinatario=nomeDestinatario;
    }
}
