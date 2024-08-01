package br.com.app.financeiro.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.app.financeiro.config.jwt.JwtUtils;
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Usuario;
import br.com.app.financeiro.service.CategoriaService;
import br.com.app.financeiro.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UsuárioController", description = "APIs relacionadas aos usuários")
@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService ;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "Adicionar um novo usuário" , method = "POST")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuário adicionado com sucesso!"
        )
    })
    @PostMapping("/adicionar")
    public ResponseEntity<String> adicionar(@RequestBody Usuario usuario){
        try{
        Long u = usuarioService.adicionarUsuario(usuario);
        usuario.setId(u);
        categoriaService.adicionarCategoriasPadroes(usuario);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao adicionar usuário: " + e);
        }
        return new ResponseEntity<>("Usuário adicionado com sucesso!", HttpStatus.CREATED);
    }

    @Operation(summary = "Adicionar mais valor ao saldo")
    @PutMapping("/adicionarSaldo")
    public ResponseEntity<String> adicionarSaldo(@RequestHeader("Authorization") String token, 
    @RequestParam("valor") BigDecimal valor) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        usuarioService.adicionarSaldo(userId,valor);
        }
        catch(Exception e ){
            e.printStackTrace();
            throw new FinanceiroException("Dados inválidos: " + e);
        }
        return new ResponseEntity<>("Saldo atualizado com sucesso!", HttpStatus.OK);
    }

   

    @GetMapping("/listar")
    public List<Usuario> listarContas() {
        return usuarioService.listarContas();
    }

    @Operation(summary = "Mostrar saldo")
    @GetMapping("/saldo")
    public BigDecimal retornarConta(@RequestHeader("Authorization") String token) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        return usuarioService.retornarSaldo(userId);
        }
        catch(Exception e){
            throw new FinanceiroException("Id inválido ou usuário inexistente",e);
        }
    }

    @Operation(summary = "Mostrar todas as informações de um usuário")
    @GetMapping("/informações")
    public String retornarInformacoes(@RequestHeader("Authorization") String token) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        
        // Obtém as informações do usuário
        return usuarioService.retornarInformacoes(userId).toString();
        }
        catch(Exception e){
        throw new FinanceiroException("Erro ao retornar informações: " + e);
        }
    }

    @Operation(summary = "Atualiza as informacoes do usuario")
    @PutMapping("/atualizarInformacoes")
    public ResponseEntity<?> atualizarInformacoes(@RequestHeader("Authorization") String token,
    @RequestBody Usuario usuario) {
        try{
        String cleanToken = token.replace("Bearer ", "");

        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);

        Long userId = Long.parseLong(userIdStr);
        
        usuario.setId(userId);
        usuarioService.atualizarInformacoes(usuario);
        return new ResponseEntity<>("Usuario atualizado com sucesso", HttpStatus.OK);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao atualizar informações",e);
        }
    }
}
