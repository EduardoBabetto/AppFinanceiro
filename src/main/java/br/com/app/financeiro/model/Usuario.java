package br.com.app.financeiro.model;

import java.math.BigDecimal;

public class Usuario {
    private Long id;
    private String nome;
    private String email ;
    private String senha ;
    private BigDecimal saldo;
    private BigDecimal despesas;
    private BigDecimal receitas;


    public Usuario() {}
    public Usuario(String email, String senha, Long id, String nome, BigDecimal saldo , BigDecimal despesas, BigDecimal receitas) {
        this.despesas = despesas;
        this.receitas = receitas;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.id = id;
        this.saldo = saldo;
    }

    public String toString(){
       return ("nome: " + this.nome +"\n"+ " email: " + this.email +"\n"+ 
        " id: " + this.id +"\n"+ " saldo: " + this.saldo +"\n"+ " despesas: " + this.despesas +"\n"+
        " receitas: " + this.receitas);
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return this.senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;    
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getSaldo() {
        return this.saldo;
    }
    public void setSaldo(BigDecimal saldo) {    
        this.saldo = saldo;
    }

    public BigDecimal getDespesas() {
        return this.despesas;
    }
    public void setDespesas(BigDecimal despesas) {
        this.despesas = despesas;
    }

    public BigDecimal getReceitas() {
        return this.receitas;
    }
    public void setReceitas(BigDecimal receitas) {
        this.receitas = receitas;
    }

}
