package br.com.app.financeiro.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.app.financeiro.dao.UsuarioDao;
import br.com.app.financeiro.err.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Transferencia;
import br.com.app.financeiro.model.Usuario;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioDao usuarioDao, PasswordEncoder passwordEncoder){
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }

    public Long adicionarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        Usuario u = usuarioDao.findbyEmail(usuario.getEmail());
        if(u != null){
            throw new FinanceiroException("Email já cadastrado",HttpStatus.CONFLICT);
        }
        try {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            return usuarioDao.adicionarUsuario(usuario);
        } catch (FinanceiroException ex) {
            logger.error("Erro ao adicionar usuário: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao adicionar usuário", ex);
            throw new FinanceiroException("Erro ao adicionar usuário.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Usuario> listarContas() {
        try {
            return usuarioDao.listarTodos();
        } catch (Exception ex) {
            logger.error("Erro ao listar contas", ex);
            throw new FinanceiroException("Erro ao listar contas.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void adicionarSaldo(Long id, BigDecimal novoSaldo) {
        try {
            usuarioDao.adicionarSaldo(id, novoSaldo);
        } catch (FinanceiroException ex) {
            logger.error("Erro ao adicionar saldo: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao adicionar saldo", ex);
            throw new FinanceiroException("Conta não encontrada!",HttpStatus.NOT_FOUND);
        }
    }

    public Usuario retornarInformacoes(Long idUsuario) {
        try {
            Usuario u = usuarioDao.procurarUsuarioPorId(idUsuario);
            if (u != null) {
                return u;
            }
            return null;
        } catch (FinanceiroException ex) {
            logger.error("Erro ao retornar informações do usuário: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao retornar informações do usuário", ex);
            throw new FinanceiroException("Conta não encontrada!",HttpStatus.NOT_FOUND);
        }
    }


    public void adicionarDespesa(BigDecimal valor, Long id) {
        Usuario usuario = usuarioDao.procurarUsuarioPorId(id);
            if(usuario.getSaldo().compareTo(valor) < 0) {
                throw new FinanceiroException("Saldo insuficiente",HttpStatus.CONFLICT);
            }
        try {
            usuarioDao.adicionarDespesa(valor, usuario);
        } catch (Exception ex) {
            logger.error("Erro ao adicionar despesa", ex);
            throw new FinanceiroException("Erro ao adicionar despesa.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void removerDespesa(BigDecimal valor, Long id) {
        try {
            usuarioDao.retirarDespesa(valor, id);
        } catch (Exception ex) {
            logger.error("Erro ao remover despesa", ex);
            throw new FinanceiroException("Erro ao remover despesa.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void atualizarReceita(Transferencia t) {
        try {
            Usuario usuario = retornarInformacoes(t.getUsuarioId());
            usuario.setSaldo(usuario.getSaldo().add(t.getValor()));
            usuarioDao.atualizarReceita(t.getValor(), t.getUsuarioId(), usuario);
        } catch (FinanceiroException ex) {
            logger.error("Erro ao atualizar receita: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao atualizar receita", ex);
            throw new FinanceiroException("Erro ao atualizar receita.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void atualizarInformacoes(Usuario usuario) {
        validarUsuario(usuario);
        Usuario u = retornarInformacoes(usuario.getId());
        if(usuarioDao.findbyEmail(usuario.getEmail()) != null) {
            throw new FinanceiroException("Email ja cadastrado", HttpStatus.CONFLICT);
        }
        try {
            if (u != null) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
                usuarioDao.atualizarInformacoes(usuario);
            } else {
                throw new FinanceiroException("Conta não encontrada!",HttpStatus.NOT_FOUND);
            }
        } catch (FinanceiroException ex) {
            logger.error("Erro ao atualizar informações do usuário: {}", ex.getMessage(), ex);
            throw new FinanceiroException("Erro ao atualizar informações do usuário.", ex.getErrorCode(), ex,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("Erro inesperado ao atualizar informações do usuário", ex);
            throw new FinanceiroException("Erro ao atualizar informações do usuário.",ex,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new FinanceiroException("Usuário não pode ser nulo.",HttpStatus.CONFLICT);
        }
        validarEmail(usuario.getEmail());
        validarSenha(usuario.getSenha());
        if (usuario.getNome() == null || usuario.getNome().isEmpty() || usuario.getNome().isBlank() || 
            !usuario.getNome().matches("^[A-Za-zÀ-ÖØ-öø-ÿÇç ]+$")) {
            throw new FinanceiroException("Nome não pode ser nulo ou vazio e nem conter números.",HttpStatus.CONFLICT);
        }
    }

    private void validarEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new FinanceiroException("Email inválido.",HttpStatus.CONFLICT);
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < MIN_PASSWORD_LENGTH) {
            throw new FinanceiroException("Senha deve ter pelo menos " + MIN_PASSWORD_LENGTH + " caracteres.",HttpStatus.CONFLICT);
        }
    }
}
