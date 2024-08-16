package br.com.app.financeiro.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.app.financeiro.err.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Usuario;

@Service
public class ConversaoMoedasService {

    private static final Logger logger = LoggerFactory.getLogger(ConversaoMoedasService.class);

    private final UsuarioService usuarioService;
    private final APIConversao apiConversao;

    public ConversaoMoedasService(UsuarioService usuarioService, APIConversao apiConversao) {
        this.usuarioService = usuarioService;
        this.apiConversao = apiConversao;
    }

    public BigDecimal taxaDeConversaoSaldo(String moeda, Long idUsuario) {
        validarMoeda(moeda);
        Usuario usuario = usuarioService.retornarInformacoes(idUsuario);
        if (usuario == null) {
            throw new FinanceiroException("Usuário não encontrado.",HttpStatus.NOT_FOUND);
        }

        try {
            BigDecimal taxaConversao = apiConversao.getCotacao(moeda);
            BigDecimal resultado = limitTrailingZeros(usuario.getSaldo().multiply(taxaConversao), 2);
            return resultado;
        }
        catch (Exception e) {
            logger.error("Erro inesperado ao converter saldo para a moeda {}: {}", moeda, e.getMessage(), e);
            throw new FinanceiroException("Erro ao converter saldo.", e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public BigDecimal valorEmReais(String moeda, Long idUsuario) {
        validarMoeda(moeda);
        Usuario usuario = usuarioService.retornarInformacoes(idUsuario);
        if (usuario == null) {
            throw new FinanceiroException("Usuário não encontrado.",HttpStatus.NOT_FOUND);
        }

        try {
            BigDecimal valorEmReais = apiConversao.getCotacao(moeda);
            return usuario.getSaldo().divide(valorEmReais, BigDecimal.ROUND_HALF_UP);
        }
        catch (Exception e) {
            logger.error("Erro inesperado ao converter valor em reais para a moeda {}: {}", moeda, e.getMessage(), e);
            throw new FinanceiroException("Erro ao converter valor.", e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public BigDecimal taxaDeConversaoSaldo(String moeda, BigDecimal valor) {
        validarMoeda(moeda);

        try {
            BigDecimal taxaConversao = apiConversao.getCotacao(moeda);
            BigDecimal resultado = limitTrailingZeros(valor.multiply(taxaConversao), 2);
            return resultado;
        }
        catch (Exception e) {
            logger.error("Erro inesperado ao converter valor {} para a moeda {}: {}", valor, moeda, e.getMessage(), e);
            throw new FinanceiroException("Erro ao converter valor.", e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public BigDecimal valorEmReais(String moeda, BigDecimal valor) {
        validarMoeda(moeda);

        try {
            BigDecimal valorEmReais = apiConversao.getCotacao(moeda);
            return valor.divide(valorEmReais, BigDecimal.ROUND_HALF_UP);
        }
        catch (Exception e) {
            logger.error("Erro inesperado ao converter valor {} em reais para a moeda {}: {}", valor, moeda, e.getMessage(), e);
            throw new FinanceiroException("Erro ao converter valor.", e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validarMoeda(String moeda) {
        if (moeda == null || moeda.trim().isEmpty()) {
            throw new FinanceiroException("A moeda não pode ser nula ou vazia.",HttpStatus.BAD_REQUEST);
        }
        String moedaRegex = "^[A-Za-zÀ-ÖØ-öø-ÿÇç]+$";
        if (!moeda.matches(moedaRegex)) {
            throw new FinanceiroException("A moeda deve conter apenas letras (maiúsculas e minúsculas),"+ 
            "incluindo caracteres acentuados e ç.",HttpStatus.CONFLICT);
        }
    }

    public BigDecimal limitTrailingZeros(BigDecimal number, int maxTrailingZeros) {
        BigDecimal stripped = number.stripTrailingZeros();
        int scale = stripped.scale() > maxTrailingZeros ? maxTrailingZeros : stripped.scale();
        return stripped.setScale(scale, RoundingMode.DOWN);
    }
}