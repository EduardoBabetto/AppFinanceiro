package br.com.app.financeiro.controller;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.app.financeiro.config.jwt.JwtUtils;
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.ConversaoMoedas;
import br.com.app.financeiro.service.ConversaoMoedasService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ConversaoMoedasController", description = "APIs relacionadas as conversões de moedas")
@RestController
@RequestMapping("/ConversaoMoedas")
public class ConversaoMoedasController {
    
    @Autowired
    private ConversaoMoedasService cms;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "Mostrar taxa de conversão do saldo do usuário")
    @PostMapping("/TaxaDeConversaoSaldo")
    public ResponseEntity<String> taxaDeConversaoSaldo(@RequestHeader("Authorization") String token, 
    @RequestParam("moeda") String moeda) throws IOException {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        
        BigDecimal saldoConvertido = cms.taxaDeConversaoSaldo(moeda,userId);
        return new ResponseEntity<>(saldoConvertido+" "+moeda,HttpStatus.OK);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao mostrar conversão: " + e);
        }
    }

    @Operation(summary = "Mostrar a conversão do saldo do usuário em outras moedas")
    @PostMapping("/SaldoEmOutrasMoedas")
    public ResponseEntity<String> SaldoEmOutrasMoedas(@RequestHeader("Authorization") String token,
    @RequestParam("moeda") String moeda) throws IOException {
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        BigDecimal saldoConvertido = cms.valorEmReais(moeda,userId);
        return new ResponseEntity<>(saldoConvertido+" "+moeda,HttpStatus.OK);
    }

    @Operation(summary = "Mostrar valor em outras moedas")
    @PostMapping("/ValorEmReais")
    public ResponseEntity<String> valorEmReais(@RequestBody ConversaoMoedas cm) throws IOException {
        BigDecimal valorConvertido = cms.valorEmReais(cm.getMoeda(),cm.getValor());
        return new ResponseEntity<>(valorConvertido+" "+cm.getMoeda(),HttpStatus.OK);
    }
    
    @Operation(summary = "Mostra a taxa de conversão do valor em outras moedas")
    @PostMapping("/TaxaDeConversao")
    public ResponseEntity<String> taxaDeConversaoValor(@RequestBody ConversaoMoedas cm) throws IOException {
        BigDecimal valorConvertido = cms.taxaDeConversaoSaldo(cm.getMoeda(),cm.getValor());
        return new ResponseEntity<>(valorConvertido+" "+cm.getMoeda(),HttpStatus.OK);
    }
}
