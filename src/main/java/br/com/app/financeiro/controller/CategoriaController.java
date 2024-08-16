package br.com.app.financeiro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.app.financeiro.config.jwt.JwtUtils;
import br.com.app.financeiro.model.Categoria;
import br.com.app.financeiro.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CategoriaController", description = "APIs relacionadas a categorias")
@RestController
@RequestMapping("/categoria")
public class CategoriaController {
    
        @Autowired
        private CategoriaService categoriaService;

        @Autowired
        private JwtUtils jwtUtils;
    
    @Operation(summary = "Adicionar uma nova categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoria adicionada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao adicionar categoria")
    })
    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionarCategoriaNova(@RequestHeader("Authorization") String token,
    @RequestBody Categoria categoria) {
    
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        categoria.setUsuarioId(userId);
        
        categoria.setId(categoriaService.adicionarCategoria(categoria));
        return new ResponseEntity<>(categoria,HttpStatus.CREATED);
    }

    @Operation(summary = "Remover uma categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Removida com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao remover categoria")
    })
    @DeleteMapping("/remover")
    public ResponseEntity<String> removerCategoria(@RequestHeader("Authorization") String token,
    @RequestParam ("nome") String nome) {
        
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setUsuarioId(userId);
        categoriaService.removerCategoria(categoria);
      
        return new ResponseEntity<>("Categoria removida com sucesso!",HttpStatus.OK);
    }

    @Operation(summary = "Adiciona valor a uma categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Valor atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao adicionar")
    })
    @PutMapping("/AdicionarValor")
    public ResponseEntity<String> adicionarValor(@RequestHeader("Authorization") String token,
    @RequestBody Categoria categoria) {
        
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        categoria.setUsuarioId(userId);
        categoriaService.adicionarValor(categoria);
      
        return new ResponseEntity<>("Valor atualizado com sucesso!",HttpStatus.OK);

    }

    @Operation(summary = "Remove um valor da categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Valor removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao remover")
    })
    @PutMapping("/removerValor")
    public ResponseEntity<String> removerValor(@RequestHeader("Authorization") String token,
    @RequestBody Categoria categoria) {
    
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        categoria.setUsuarioId(userId);
        categoriaService.removerValor(categoria);
       
        return new ResponseEntity<>("Valor removido com sucesso!",HttpStatus.OK);
    }

    @Operation(summary = "Mostra as categorias de um usuário escolhido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao mostrar categorias")
    })
    @GetMapping("/CategoriaPorUsuario")
    public ResponseEntity<List<Categoria>> categoriaPorUsuario(@RequestHeader("Authorization") String token) {
     
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        List<Categoria> categorias = categoriaService.categoriaPorUsuario(userId);
        return new ResponseEntity<>(categorias,HttpStatus.OK);
       
    }

    @Operation(summary = "Edita a categoria escolhida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorias editadas com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao editar categoria")
    })
    @PutMapping("/editar")
    public ResponseEntity<?> editarCategoria(@RequestHeader ("Authorization") String token,
    @RequestBody Categoria categoria) {
   
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        categoria.setUsuarioId(userId);
        categoriaService.editarCategoria(categoria);
      
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
