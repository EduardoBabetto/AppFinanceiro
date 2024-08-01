package br.com.app.financeiro.config.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtils {

    @Value("${projeto.jwtSecret}")
    private String jwtSecret;

    @Value("${projeto.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateTokenFromUserDetailsImpl(UserDetailsImpl userDetail){
            Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetail.getUsuario().getId());
        claims.put("nome", userDetail.getUsuario().getNome());
        claims.put("email", userDetail.getUsuario().getEmail());
        claims.put("password", userDetail.getUsuario().getSenha());

        return Jwts.builder().setClaims(claims)
        .setSubject(userDetail.getUsername())
        .setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public Key getSigningKey() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        return key;
    }

    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        }
        catch(MalformedJwtException e){
            throw new FinanceiroException("Token inválido"+ e.getMessage());
        }
        catch(Exception e){
            throw new FinanceiroException("Token inválido"+ e.getMessage());
        }
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

public Claims verifyToken(String token) {
    return Jwts.parser()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
}

public String getUserIdFromToken(String token) {
    Claims claims = verifyToken(token);
    // Verifica se a claim 'userId' é do tipo Integer e converte para String
    Object userIdObj = claims.get("userId");
    if (userIdObj instanceof Integer) {
        return String.valueOf(userIdObj);
    } else if (userIdObj instanceof String) {
        return (String) userIdObj;
    } else {
        throw new IllegalArgumentException("Invalid type for userId claim");
    }
}


public String getNomeFromToken(String token) {
    Claims claims = verifyToken(token);
    return claims.get("nome", String.class);
}


}
