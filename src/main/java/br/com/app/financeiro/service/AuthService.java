package br.com.app.financeiro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.app.financeiro.config.jwt.JwtUtils;
import br.com.app.financeiro.dao.UsuarioDao;
import br.com.app.financeiro.dto.AcessDTO;
import br.com.app.financeiro.dto.AuthenticationDTO;
import br.com.app.financeiro.err.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Usuario;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    private final UsuarioDao usuarioDao;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtils jwtUtils, UsuarioDao usuarioDao, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }


    public AcessDTO login(AuthenticationDTO authDTO) {
        logger.info("Tentativa de login para o usuário: {}", authDTO.getUsername());
        Usuario usuario = validacao(authDTO.getUsername(), authDTO.getPassword());
        try {

            logger.info("Login bem-sucedido para o usuário: {}", authDTO.getUsername());

            return jwtUtils.getToken(usuario);
        } catch (BadCredentialsException e) {
            logger.error("Credenciais inválidas para o usuário: {}", authDTO.getUsername(), e);
            throw new FinanceiroException("Usuário ou senha inválidos", e, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Erro inesperado durante o login do usuário: {}", authDTO.getUsername(), e);
            throw new FinanceiroException("Erro inesperado durante o login", e, HttpStatus.BAD_REQUEST);
        }
    }

    public Usuario validacao(String email, String senha) {
        logger.info("Validando credenciais para o email: {}", email);
        try {
            validarEmail(email);
            validarSenha(senha);

            Usuario usuario = usuarioDao.findbyEmail(email);
            if (usuario == null) {
                logger.warn("Email não registrado: {}", email);
                throw new FinanceiroException("Email não registrado.",HttpStatus.NOT_FOUND);
            } else if (!passwordEncoder.matches(senha, usuario.getSenha())) {
                logger.warn("Senha inválida para o email: {}", email);
                throw new FinanceiroException("Senha inválida.", HttpStatus.NOT_ACCEPTABLE);
            }
            logger.info("Credenciais válidas para o email: {}", email);
            return usuario;
        } catch (FinanceiroException e) {
            logger.error("Erro ao validar credenciais para o email: {}", email, e);
            throw e;
        }
    }

    private void validarEmail(String email) {
        logger.debug("Validando formato do email: {}", email);
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            logger.warn("Formato de email inválido: {}", email);
            throw new FinanceiroException("Email inválido.",HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private void validarSenha(String senha) {
        logger.debug("Validando tamanho da senha");
        if (senha == null || senha.length() < 8) {
            logger.warn("Senha inválida: Menor que 8 caracteres.");
            throw new FinanceiroException("Senha deve ter pelo menos " + 8 + " caracteres.", HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
