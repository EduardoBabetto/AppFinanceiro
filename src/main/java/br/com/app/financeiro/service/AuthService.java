package br.com.app.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Usuario;

@Service
public class AuthService {
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    private final UsuarioDao usuarioDao;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UsuarioDao usuarioDao, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }
    
    public AuthenticationDTO validateUser(AuthenticationDTO authDTO) {
        Usuario u = validacao(authDTO.getUsername(), authDTO.getPassword());
        AuthenticationDTO auth = new AuthenticationDTO();
        auth.setUsername(u.getEmail());
        auth.setPassword(authDTO.getPassword());
        return auth;
    }
    
    public AcessDTO login(AuthenticationDTO authDTO) {
        try{

        AuthenticationDTO auth = validateUser(authDTO);

        //Cria mecanismo de credencial para o spring
        UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
            auth.getUsername(), auth.getPassword()
        );

        //Prepara mecanismo para autenticação
        Authentication authentication = authenticationManager.authenticate(userAuth);

        //Busca usuario logado
        UserDetailsImpl userAuthenticate = (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtUtils.generateTokenFromUserDetailsImpl(userAuthenticate);

        AcessDTO acess = new AcessDTO(token);
        return acess;
        }
        catch(BadCredentialsException e) {
            e.printStackTrace();
            throw new FinanceiroException("Usuário ou senha inválidos",e);
        }
    }

     public Usuario validacao(String email, String senha) {
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

    private void validarEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new FinanceiroException("Email inválido.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() <8) {
            throw new FinanceiroException("Senha deve ter pelo menos " + 8 + " caracteres.");
        }
    }
}
