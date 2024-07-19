package ru.golovkov.myrestapp.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt-secret}")
    private String secret;

    private static final String ISSUER = "igor";
    private static final String SUBJECT = "User Details";
    private static final String CLAIM_NAME = "username";

    public String generateToken(String username) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
        return JWT
                .create()
                .withSubject(SUBJECT)
                .withClaim(CLAIM_NAME, username)
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String verifyTokenAndGetUsernameClaim(String token) {
        JWTVerifier jwtVerifier = JWT
                .require(Algorithm.HMAC256(secret))
                .withSubject(SUBJECT)
                .withIssuer(ISSUER)
                .build();
        DecodedJWT decodedJwt = jwtVerifier
                .verify(token);
        return decodedJwt.getClaim(CLAIM_NAME).asString();
    }
}
