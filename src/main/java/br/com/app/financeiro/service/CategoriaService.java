package br.com.app.financeiro.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.app.financeiro.dao.CategoriaDao;
import br.com.app.financeiro.err.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Categoria;
import br.com.app.financeiro.model.Usuario;

@Service
public class CategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaDao categoriaDao;
    private final UsuarioService usuarioService;

    public CategoriaService(CategoriaDao categoriaDao, UsuarioService usuarioService) {
        this.categoriaDao = categoriaDao;
        this.usuarioService = usuarioService;
    }

    public void adicionarCategoriasPadroes(Usuario usuario) {
        try {
            if (usuario == null || usuario.getId() == null) {
                throw new FinanceiroException("Usuário inválido.",HttpStatus.BAD_REQUEST);
            }
            categoriaDao.adicionarCategoriasPadrao(usuario);
        } catch (FinanceiroException ex) {
            logger.error("Erro ao adicionar categorias padrões: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao adicionar categorias padrões", ex);
            throw new FinanceiroException("Erro ao adicionar categorias padrões.",HttpStatus.BAD_REQUEST);
        }
    }

    public Long adicionarCategoria(Categoria categoria) {
        try {
            validarCategoria(categoria);
            Long id = categoriaDao.adicionarCategoria(categoria);
            adicionarValor(categoria);
            return id;
        } catch (FinanceiroException ex) {
            logger.error("Erro ao adicionar categoria: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao adicionar categoria", ex);
            throw new FinanceiroException("Erro ao adicionar categoria.",HttpStatus.BAD_REQUEST);
        }
    }

    public void removerCategoria(Categoria categoria) {
        try {
            validarCategoria(categoria);
            categoriaDao.removerCategoria(categoria);
        } catch (FinanceiroException ex) {
            logger.error("Erro ao remover categoria: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao remover categoria", ex);
            throw new FinanceiroException("Erro ao remover categoria.",HttpStatus.BAD_REQUEST);
        }
    }

    public void adicionarValor(Categoria categoria) {
        try {
            validarCategoria(categoria);
            Usuario u = usuarioService.retornarInformacoes(categoria.getUsuarioId());
            if (categoria.getValor().compareTo(u.getSaldo()) > 0) {
                throw new FinanceiroException("Não pode remover valor maior que o seu saldo",HttpStatus.CONFLICT);
            }
            categoriaDao.adicionarValor(categoria);
            usuarioService.adicionarDespesa(categoria.getValor(), categoria.getUsuarioId());
        } catch (FinanceiroException ex) {
            logger.error("Erro ao adicionar valor à categoria: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao adicionar valor à categoria", ex);
            throw new FinanceiroException("Erro ao adicionar valor à categoria.",HttpStatus.BAD_REQUEST);
        }
    }

    public void removerValor(Categoria categoria) {
        try {
            validarCategoria(categoria);
            Categoria c = categoriaDao.buscarCategoria(categoria.getUsuarioId(), categoria.getNome());
            if (categoria.getValor().compareTo(c.getValor()) > 0) {
                throw new FinanceiroException("Não pode remover valor maior que o valor da categoria",HttpStatus.CONFLICT);
            } else {
                categoriaDao.removerValor(categoria);
                usuarioService.removerDespesa(categoria.getValor(), categoria.getUsuarioId());
            }
        } catch (FinanceiroException ex) {
            logger.error("Erro ao remover valor da categoria: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao remover valor da categoria", ex);
            throw new FinanceiroException("Erro ao remover valor da categoria.",HttpStatus.BAD_REQUEST);
        }
    }

    public List<Categoria> categoriaPorUsuario(Long id) {
        try {
            if (id == null) {
                throw new FinanceiroException("ID do usuário inválido.",HttpStatus.NOT_FOUND);
            }
            return categoriaDao.categoriaPorUsuario(id);
        } catch (FinanceiroException ex) {
            logger.error("Erro ao buscar categorias por usuário: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao buscar categorias por usuário", ex);
            throw new FinanceiroException("Erro ao buscar categorias por usuário.",HttpStatus.BAD_REQUEST);
        }
    }

    public void editarCategoria(Categoria categoria) {
        try {
            validarCategoria(categoria);
            categoriaDao.editarCategoria(categoria);
            if (categoria.getValor().compareTo(BigDecimal.ZERO) > 0) {
                usuarioService.adicionarDespesa(categoria.getValor(), categoria.getUsuarioId());
            } else if (categoria.getValor().compareTo(BigDecimal.ZERO) < 0) {
                categoria.setValor(categoria.getValor().negate());
                usuarioService.removerDespesa(categoria.getValor(), categoria.getUsuarioId());
            }
        } catch (FinanceiroException ex) {
            logger.error("Erro ao editar categoria: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao editar categoria", ex);
            throw new FinanceiroException("Erro ao editar categoria.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new FinanceiroException("Categoria não pode ser nula.",HttpStatus.CONFLICT);
        }
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new FinanceiroException("Nome da categoria não pode ser nulo ou vazio.",HttpStatus.CONFLICT);
        }
        String nomeRegex = "^[A-Za-zÀ-ÖØ-öø-ÿÇç\\s]+$";
        if (!categoria.getNome().matches(nomeRegex)) {
            throw new FinanceiroException("Nome da categoria deve conter apenas letras (maiúsculas e minúsculas),"+
             "incluindo caracteres acentuados e ç.",HttpStatus.CONFLICT);
        }
        if (categoria.getUsuarioId() == null) {
            throw new FinanceiroException("ID do usuário da categoria não pode ser nulo.",HttpStatus.BAD_REQUEST);
        }
        Usuario usuario = usuarioService.retornarInformacoes(categoria.getUsuarioId());
        if (usuario == null) {
            throw new FinanceiroException("Usuário associado à categoria não encontrado.",HttpStatus.BAD_REQUEST);
        }
    }
}
