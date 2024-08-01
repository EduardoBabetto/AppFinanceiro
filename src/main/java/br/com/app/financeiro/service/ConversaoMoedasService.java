package br.com.app.financeiro.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Usuario;

@Service
public class ConversaoMoedasService {
    private final UsuarioService us;
    private final APIConversao api;

    public ConversaoMoedasService( UsuarioService us, APIConversao api) {
        this.us = us;
        this.api = api;
    }


    public BigDecimal taxaDeConversaoSaldo(String moeda, Long idUsuario) throws IOException {
        validarMoeda(moeda);
        Usuario usuario = us.retornarInformacoes(idUsuario);
        if (usuario == null) {
            throw new FinanceiroException("Usuário não encontrado.");
        }
        BigDecimal taxaConversao = api.getCotacao(moeda);
        if (taxaConversao == null) {
            throw new FinanceiroException("Taxa de conversão não encontrada para a moeda: " + moeda);
        }
        BigDecimal resultado = limitTrailingZeros(usuario.getSaldo().multiply(taxaConversao), 2);
        return resultado;
    }

    public BigDecimal valorEmReais(String moeda, Long idUsuario) throws IOException {
        validarMoeda(moeda);
        Usuario usuario = us.retornarInformacoes(idUsuario);
        if (usuario == null) {
            throw new FinanceiroException("Usuário não encontrado.");
        }
        BigDecimal valorEmReais = api.getCotacao(moeda);
        if (valorEmReais == null) {
            throw new FinanceiroException("Valor em reais não encontrado para a moeda: " + moeda);
        }
        return usuario.getSaldo().divide(valorEmReais, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal taxaDeConversaoSaldo(String moeda, BigDecimal valor) throws IOException {
        validarMoeda(moeda);
        
        BigDecimal taxaConversao = api.getCotacao(moeda);
        if (taxaConversao == null) {
            throw new FinanceiroException("Taxa de conversão não encontrada para a moeda: " + moeda);
        }
        BigDecimal resultado = limitTrailingZeros(valor.multiply(taxaConversao), 2);
        return resultado;
    }

    public BigDecimal valorEmReais(String moeda, BigDecimal valor) throws IOException {
        validarMoeda(moeda);
    
        BigDecimal valorEmReais = api.getCotacao(moeda);
        if (valorEmReais == null) {
            throw new FinanceiroException("Valor em reais não encontrado para a moeda: " + moeda);
        }
        return valor.divide(valorEmReais, BigDecimal.ROUND_HALF_UP);
    }

    private void validarMoeda(String moeda) {
        if (moeda == null || moeda.trim().isEmpty()) {
            throw new FinanceiroException("A moeda não pode ser nula ou vazia.");
        }
        String moedaRegex = "^[A-Za-zÀ-ÖØ-öø-ÿÇç]+$";
        if (!moeda.matches(moedaRegex)) {
            throw new FinanceiroException("A moeda deve conter apenas letras (maiúsculas e minúsculas), incluindo caracteres acentuados e ç.");
        }
    }

     public BigDecimal limitTrailingZeros(BigDecimal number, int maxTrailingZeros) {
        // Remove todos os zeros à direita
        BigDecimal stripped = number.stripTrailingZeros();
        
        // Ajusta a escala para o máximo permitido de zeros à direita
        int scale = stripped.scale() > maxTrailingZeros ? maxTrailingZeros : stripped.scale();
        return stripped.setScale(scale, RoundingMode.DOWN);
    }
}