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
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Categoria;
import br.com.app.financeiro.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping("/adicionar")
    public ResponseEntity<String> adicionarCategoriaNova(@RequestHeader("Authorization") String token,
    @RequestBody Categoria categoria) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        categoria.setUsuarioId(userId);
        categoriaService.adicionarCategoria(categoria);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao adicionar a categoria",e);
        }
        return new ResponseEntity<>("Categoria adicionada com sucesso!",HttpStatus.CREATED);
    }

    @Operation(summary = "Remover uma categoria")
    @DeleteMapping("/remover")
    public ResponseEntity<String> removerCategoria(@RequestHeader("Authorization") String token,
    @RequestParam ("nome") String nome) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);

        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setUsuarioId(userId);
        categoriaService.removerCategoria(categoria);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao remover categoria",e);
        }
        return new ResponseEntity<>("Categoria removida com sucesso!",HttpStatus.OK);
    }

    @Operation(summary = "Adiciona valor a uma categoria")
    @PutMapping("/AdicionarValor")
    public ResponseEntity<String> adicionarValor(@RequestHeader("Authorization") String token,
    @RequestBody Categoria categoria) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        categoria.setUsuarioId(userId);
        categoriaService.adicionarValor(categoria);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao adicionar valor",e);
        }
        return new ResponseEntity<>("Valor atualizado com sucesso!",HttpStatus.OK);

    }

    @Operation(summary = "Remove um valor da categoria")
    @PutMapping("/removerValor")
    public ResponseEntity<String> removerValor(@RequestHeader("Authorization") String token,
    @RequestBody Categoria categoria) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        categoria.setUsuarioId(userId);
        categoriaService.removerValor(categoria);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao remover valor",e);
        }
        return new ResponseEntity<>("Valor removido com sucesso!",HttpStatus.OK);
    }

    @Operation(summary = "Mostra as categorias de um usuário escolhido")
    @GetMapping("/CategoriaPorUsuario")
    public ResponseEntity<List<Categoria>> categoriaPorUsuario(@RequestHeader("Authorization") String token) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        List<Categoria> categorias = categoriaService.categoriaPorUsuario(userId);
        return new ResponseEntity<>(categorias,HttpStatus.OK);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao mostrar categorias",e);
        }
    }

    @Operation(summary = "Edita a categoria escolhida")
    @PutMapping("/editar")
    public ResponseEntity<?> editarCategoria(@RequestHeader ("Authorization") String token,
    @RequestBody Categoria categoria) {
        try{
        String cleanToken = token.replace("Bearer ", "");
        // Extrai o userId do token
        String userIdStr = jwtUtils.getUserIdFromToken(cleanToken);
        // Converte o userId para Long
        Long userId = Long.parseLong(userIdStr);
        categoria.setUsuarioId(userId);
        categoriaService.editarCategoria(categoria);
        }
        catch(Exception e){
            throw new FinanceiroException("Erro ao editar categoria",e);
        }
        return new ResponseEntity<>("Categoria editada com sucesso!",HttpStatus.OK);
    }
    
}
