package com.ltp.banksystem.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final String secretKey;

    public JwtTokenService() {
        secretKey = generateSecretKey();
    }

    public String generateJwtToken(final String username) {
        final Map<String, Object> claims = new HashMap<>();
        final int timeInMinutes = 10;
        final int timeInMilliseconds = 1000 * 60 * timeInMinutes;
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + timeInMilliseconds))
                .signWith(getKey())
                .compact();

    }

    private String generateSecretKey() {
        final byte[] secretKey = new byte[32];
        new SecureRandom().nextBytes(secretKey);
        return Base64.getEncoder().encodeToString(secretKey);
    }

    private SecretKey getKey() {
        final byte[] decodeKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decodeKey);
    }

    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(final String token, final Function<Claims,T> tokenResolver) {
        final Claims claims = extractAllClaims(token);
        return tokenResolver.apply(claims);
    }

    private Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(final String token,final UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }

    public boolean isExpired(final String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }
}
