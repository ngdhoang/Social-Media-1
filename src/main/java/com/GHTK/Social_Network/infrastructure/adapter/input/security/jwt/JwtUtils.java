package com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt;

import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtUtils implements JwtPort {
  @Value("${application.GHTK.JwtUtils.secretKey}")
  private String secretKey;

  @Value("${application.GHTK.JwtUtils.jwtExpiration}")
  private long jwtExpiration;

  @Value("${application.GHTK.JwtUtils.refreshExpiration}")
  private long refreshExpiration;

  public String extractUserEmail(String jwt) {
    return extractClaim(jwt, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUserEmail(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(UserDetails userDetails) {

    return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSignInKey())
            .compact();
  }

  public String generateToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails
  ) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  public String generateRefreshToken(
          UserDetails userDetails
  ) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          long expiration
  ) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  public String parseJwt(StompHeaderAccessor accessor) {
    String token = accessor.getFirstNativeHeader("Authorization");
    String jwt = null;
    if (token != null) {
      jwt = token.substring(7);
    }
    return jwt;
  }
}

