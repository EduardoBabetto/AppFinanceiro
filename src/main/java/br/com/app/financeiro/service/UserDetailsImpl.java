package br.com.app.financeiro.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.app.financeiro.model.Usuario;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Usuario usuario;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(usuario.getEmail(), that.usuario.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario.getEmail());
    }

    @Override
    public String toString() {
        return "UserDetailImpl{" +
                "username='" + usuario.getEmail() + '\'' +
                ", password='" + usuario.getSenha() + '\'' +
                ", isAccountNonExpired=true" +
                ", isAccountNonLocked=true" +
                ", isCredentialsNonExpired=true" +
                ", isEnabled=true"+
                ", authorities=null"+
                '}';
    }
}
