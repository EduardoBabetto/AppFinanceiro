package br.com.app.financeiro.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.app.financeiro.config.jwt.JwtUtils;
import br.com.app.financeiro.enuns.TipoTransferencia;
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Transferencia;
import br.com.app.financeiro.service.TransferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TransferenciaController", description = "APIs relacionadas a transferências")
@RestController
@RequestMapping("/transferencia")
public class TransferenciaController {
    
    @Autowired
    private TransferenciaService transferenciaService;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "Adiciona uma nova transferencia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transferencia adicionada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao adicionar transferencia")
    })
    @PostMapping("/adicionar")
    public ResponseEntity<String> adicionar(@RequestHeader("Authorization") String token,
    @RequestBody Transferencia transferencia) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        transferencia.setUsuarioId(userId);
        transferenciaService.adicionarTransacao(transferencia);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao adicionar transferencia", e);
        }
        return new ResponseEntity<>("Transação adicionada com sucesso!", HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public List<Transferencia> listarTransacoes() {
        return transferenciaService.listarTransacoes();
    }
    
    @Operation(summary = "Mostra todas as transferencias de um usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferencia retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao mostrar transferencia")
    })
    @GetMapping("/extrato")
    public List<Transferencia> listarTransferencias(@RequestHeader("Authorization") String token) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        return transferenciaService.extratoPorContas(userId);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao mostrar extrato",e);
        }
    }

    @Operation(summary = "Remove uma transferencia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transferencia removida com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao remover transferencia")
    })
    @DeleteMapping("/remover")
    public ResponseEntity<String> removerTransferencia(@RequestHeader("Authorization") String token,
    @RequestBody Transferencia transferencia) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        transferencia.setUsuarioId(userId);
        transferenciaService.removerTransacao(transferencia);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao remover transferencia", e);
        }
        return new ResponseEntity<>("Transação removida com sucesso!", HttpStatus.OK);
        
    }

    @Operation(summary = "Mostra todas as transferencias de um usuario em ordem de data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferencia retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao mostrar transferencia")
    })
    @GetMapping("/extrato/data")
    public List<Transferencia> extratoOrdData(@RequestHeader("Authorization") String token) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        return transferenciaService.extratoOrdData(userId);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao mostrar extrato",e);
        }
    }

    @Operation(summary = "Mostra todas as transferencias de um usuario em ordem de valor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferencia retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao mostrar transferencia")
    })
    @GetMapping("/extrato/valor")
    public List<Transferencia> extratoOrdValor(@RequestHeader("Authorization") String token) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        return transferenciaService.extratoOrdValor(userId);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao mostrar extrato",e);
        }
    }

    @Operation(summary = "Mostra todas as transferencias de um usuario filtrando por tipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferencia retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao mostrar transferencia")
    })
    @GetMapping("/extrato/filtrar/tipo")
    public List<Transferencia> extratoFiltrarTipo(@RequestHeader("Authorization") String token,
    @RequestParam("tipo") TipoTransferencia tipo) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        return transferenciaService.extratoFiltrarTipo(userId,tipo);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao mostrar extrato",e);
        }
    }

    @Operation(summary = "Mostra todas as transferencias de um usuario filtrando por categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferencia retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao mostrar transferencia")
    })
    @GetMapping("/extrato/filtrar/categoria")
    public List<Transferencia> extratoFiltrarCategoria(@RequestHeader("Authorization") String token,
    @RequestParam("categoria") String categoria) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        return transferenciaService.extratoFiltrarCategoria(userId,categoria);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao mostrar extrato",e);
        }
    }

    @Operation(summary = "Mostra todas as transferencias de um usuario filtrando por data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferencia retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao mostrar transferencia")
    })
    @GetMapping("/extrato/filtrar/data")
    public List<Transferencia> extratoFiltrarData(@RequestHeader("Authorization") String token,
    @RequestParam("data") LocalDate data) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        return transferenciaService.extratoFiltrarData(userId,data);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao mostrar extrato",e);
        }
    }
    
}
