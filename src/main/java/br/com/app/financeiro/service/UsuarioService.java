package br.com.app.financeiro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.app.financeiro.dao.UsuarioDao;
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Transferencia;
import br.com.app.financeiro.model.Usuario;

@Service
public class UsuarioService {

    
    private UsuarioDao usuarioDao;
    
    private PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioDao usuarioDao, PasswordEncoder passwordEncoder){
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }

    private static final int MIN_PASSWORD_LENGTH = 8;

    public Long adicionarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioDao.adicionarUsuario(usuario);
    }

    public List<Usuario> listarContas() {
        return usuarioDao.listarTodos();
    }

    public void adicionarSaldo(Long id, BigDecimal novoSaldo) {
        usuarioDao.adicionarSaldo(id, novoSaldo);
    }

    public BigDecimal retornarSaldo(Long id) {
        Usuario u = usuarioDao.procurarUsuarioPorId(id);
        if (u != null) {
            return u.getSaldo();
        } else {
            throw new FinanceiroException("Conta não encontrada!");
        }
    }

    public Usuario retornarInformacoes(Long idUsuario) {
        return usuarioDao.procurarUsuarioPorId(idUsuario);
    }

    public Usuario login(String email, String senha) {
        validarEmail(email);
        validarSenha(senha);

        Usuario usuario = usuarioDao.findbyEmail(email);
        if (usuario == null) {
            throw new FinanceiroException("Email não registrado.");
        } else if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new FinanceiroException("Senha inválida.");
        }
        return usuario;
    }

    public void adicionarDespesa(BigDecimal valor, Long id) {
        usuarioDao.adicionarDespesa(valor, id);
    }

    public void removerDespesa(BigDecimal valor , Long id){
        usuarioDao.retirarDespesa(valor, id);
    }

    public void atualizarReceita(Transferencia t) {
        Usuario usuario = retornarInformacoes(t.getUsuarioId());
        usuario.setSaldo(usuario.getSaldo().add(t.getValor()));
        usuarioDao.atualizarReceita(t.getValor(), t.getUsuarioId(), usuario);
    }

    public void atualizarInformacoes(Usuario usuario){
        Usuario u = retornarInformacoes(usuario.getId());
        if(u != null){
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            usuarioDao.atualizarInformacoes(usuario);
        }
        else{
            throw new FinanceiroException("Conta não encontrada!");
        }
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new FinanceiroException("Usuário não pode ser nulo.");
        }
        validarEmail(usuario.getEmail());
        validarSenha(usuario.getSenha());
        if (usuario.getNome() == null || usuario.getNome().isEmpty() || usuario.getNome().isBlank() || usuario.getNome().matches("^[A-Za-zÀ-ÖØ-öø-ÿÇç]+$")) {
            throw new FinanceiroException("Nome não pode ser nulo ou vazio.");
        }
    }

    private void validarEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new FinanceiroException("Email inválido.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < MIN_PASSWORD_LENGTH) {
            throw new FinanceiroException("Senha deve ter pelo menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }
    }
}
