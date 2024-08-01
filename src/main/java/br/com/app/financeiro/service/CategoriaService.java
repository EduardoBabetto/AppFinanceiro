package br.com.app.financeiro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.app.financeiro.dao.CategoriaDao;
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Categoria;
import br.com.app.financeiro.model.Usuario;

@Service
public class CategoriaService {

    private final CategoriaDao categoriaDao;
    private final UsuarioService usuarioService ;

    public CategoriaService(CategoriaDao categoriaDao, UsuarioService usuarioService) {
        this.categoriaDao = categoriaDao;
        this.usuarioService = usuarioService;
    }

    public void adicionarCategoriasPadroes(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new FinanceiroException("Usuário inválido.");
        }
        categoriaDao.adicionarCategoriasPadrao(usuario);
    }

    public void adicionarCategoria(Categoria categoria) {
        validarCategoria(categoria);
        categoriaDao.adicionarCategoria(categoria);
        adicionarValor(categoria);
    }

    public void removerCategoria(Categoria categoria) {
        validarCategoria(categoria);
        categoriaDao.removerCategoria(categoria);
    }

    public void adicionarValor(Categoria categoria) {
        validarCategoria(categoria);
        Usuario u = usuarioService.retornarInformacoes(categoria.getUsuarioId());
        if(categoria.getValor().compareTo(u.getSaldo()) > 0){
            throw new FinanceiroException("Não pode remover valor maior que o seu saldo");
        }
        categoriaDao.adicionarValor(categoria);
        usuarioService.adicionarDespesa(categoria.getValor(), categoria.getUsuarioId());
    }

    public void removerValor(Categoria categoria) {
        validarCategoria(categoria);
        Categoria c = categoriaDao.buscarCategoria(categoria.getUsuarioId(), categoria.getNome());
        if(categoria.getValor().compareTo(c.getValor()) > 0){
            throw new FinanceiroException("Não pode remover valor maior que o valor da categoria");
        }
        else{
        categoriaDao.removerValor(categoria);
        usuarioService.removerDespesa(categoria.getValor(), categoria.getUsuarioId());
        }
    }

    public List<Categoria> categoriaPorUsuario(Long id) {
        if (id == null) {
            throw new FinanceiroException("ID do usuário inválido.");
        }
        return categoriaDao.categoriaPorUsuario(id);
    }

    public void  editarCategoria(Categoria categoria) {
        validarCategoria(categoria);
        categoriaDao.editarCategoria(categoria);
        if(categoria.getValor().compareTo(BigDecimal.ZERO) > 0){
        usuarioService.adicionarDespesa(categoria.getValor(), categoria.getUsuarioId());
        }
        else if (categoria.getValor().compareTo(BigDecimal.ZERO) < 0) {
            categoria.getValor().negate();
            usuarioService.removerDespesa(categoria.getValor(), categoria.getUsuarioId());
        }
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new FinanceiroException("Categoria não pode ser nula.");
        }
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new FinanceiroException("Nome da categoria não pode ser nulo ou vazio.");
        }
        String nomeRegex = "^[A-Za-zÀ-ÖØ-öø-ÿÇç\\s]+$";
        if (!categoria.getNome().matches(nomeRegex)) {
            throw new FinanceiroException("Nome da categoria deve conter apenas letras (maiúsculas e minúsculas), incluindo caracteres acentuados e ç.");
        }
        if (categoria.getUsuarioId() == null) {
            throw new FinanceiroException("ID do usuário da categoria não pode ser nulo.");
        }
        Usuario usuario = usuarioService.retornarInformacoes(categoria.getUsuarioId());
        if (usuario == null) {
            throw new FinanceiroException("Usuário associado à categoria não encontrado.");
        }
    }
}
