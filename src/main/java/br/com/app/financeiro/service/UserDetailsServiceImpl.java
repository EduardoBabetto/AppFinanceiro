package br.com.app.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.app.financeiro.dao.UsuarioDao;
import br.com.app.financeiro.model.Usuario;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioDao usuarioDao;

    public UserDetailsServiceImpl(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findbyEmail(email);
        if (usuario == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + email);
        }

        return new UserDetailsImpl(usuario);
    }
}
