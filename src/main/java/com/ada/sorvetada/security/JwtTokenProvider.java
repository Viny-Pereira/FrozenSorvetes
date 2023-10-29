package com.ada.sorvetada.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.jwt.expiration-milliseconds}")
    private int jwtExpirationInMs;

    public String generateToke(Authentication authentication) {
        String userName = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build().parseClaimsJwt(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key())
                    .build().parse(token);
            return true;
        } catch (SignatureException e) {
            throw new RuntimeException("Token KWT invalid");
        } catch (MalformedJwtException e){
            throw new RuntimeException("Token invalid");
        }catch (ExpiredJwtException e){
            throw new RuntimeException("Token expired");
        }catch (UnsupportedJwtException e){
            throw new RuntimeException("Token not supported");
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Claims is empty");
        }
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }
}
