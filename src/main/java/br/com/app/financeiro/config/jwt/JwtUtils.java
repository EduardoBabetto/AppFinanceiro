package br.com.app.financeiro.config.jwt;


import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.app.financeiro.dto.AcessDTO;
import br.com.app.financeiro.model.Usuario;




@Component
public class JwtUtils {

    @Value("${projeto.issuer}")
    private String emissor;

    @Value("${projeto.jwtSecret}")
    private String jwtSecret;

    @Value("${projeto.jwtExpirationMs}")
    private int jwtExpirationMs;

    private final int ONE_MINUTE = 60000;

public AcessDTO getToken(Usuario dataAuthentication) {
        String token = JWT.create()
                .withIssuer(emissor)
                .withSubject(dataAuthentication.getEmail())
                .withClaim("user_id", dataAuthentication.getId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + (ONE_MINUTE * jwtExpirationMs)))
                .sign(Algorithm.HMAC256(jwtSecret));

        return new AcessDTO(token);
    }

 public DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer(emissor)
                .build()
                .verify(token);
    }

public String getUserIdFromToken(String token) {
    DecodedJWT decodedJWT = verifyToken(token);
    return decodedJWT.getClaim("user_id").asString();
}
}
