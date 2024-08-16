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
        ),
        @ApiResponse(responseCode="400", description = "Erro ao adicionar usuário")
    })
    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionar(@RequestBody Usuario usuario){
        Long u = usuarioService.adicionarUsuario(usuario);
        usuario.setId(u);
        categoriaService.adicionarCategoriasPadroes(usuario);

        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @Operation(summary = "Adicionar mais valor ao saldo")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Saldo adicionado com sucesso!"
        ),
        @ApiResponse(responseCode="400", description = "Erro ao adicionar saldo")
    })
    @PutMapping("/adicionarSaldo")
    public ResponseEntity<String> adicionarSaldo(@RequestHeader("Authorization") String token, 
    @RequestParam("valor") BigDecimal valor) {
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        usuarioService.adicionarSaldo(userId,valor);
        return new ResponseEntity<>("Saldo atualizado com sucesso!", HttpStatus.OK);
    }

   

    @GetMapping("/listar")
    public List<Usuario> listarContas() {
        return usuarioService.listarContas();
    }

    @Operation(summary = "Mostrar todas as informações de um usuário")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Informações retornadas com sucesso!"
        ),
        @ApiResponse(responseCode="400", description = "Erro ao mostrar informações")
    })
    @GetMapping("/informações")
    public ResponseEntity<?> retornarInformacoes(@RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        
        // Obtém as informações do usuário
        return new ResponseEntity<>(usuarioService.retornarInformacoes(userId), HttpStatus.OK);

    }

    @Operation(summary = "Atualiza as informacoes do usuario")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Informações atualizadas com sucesso!"
        ),
        @ApiResponse(responseCode="400", description = "Erro ao atualizar informações")
    })
    @PutMapping("/atualizarInformacoes")
    public ResponseEntity<?> atualizarInformacoes(@RequestHeader("Authorization") String token,
    @RequestBody Usuario usuario) {
      
        String cleanToken = token.replace("Bearer ", "");

        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);

        Long userId = Long.parseLong(userIdStr);
        
        usuario.setId(userId);
        usuarioService.atualizarInformacoes(usuario);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }
}
