package ru.sfedu.finalqualifyingwork.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secretKey;
  @Value("${jwt.header}")
  private String authorisationHeader;
  @Value("${jwt.expiration.minutes}")
  private long minutes;
  @Value("${jwt.expiration.seconds}")
  private long seconds;
  @Value("${jwt.expiration.hours}")
  private long hours;
  @Value("${jwt.expiration.days}")
  private long days;

  private final UserDetailsService userDetailsService;

  @Autowired
  public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @PostConstruct
  protected void init(){
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(String username, String role){
    Claims claims = Jwts.claims().setSubject(username);
    claims.put("role", role);
    Date now = new Date();
    Date validity = new Date(now.getTime() + toMilliseconds(seconds, minutes, hours, days));
    return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
  }

  public boolean validateToken(String token){
    try {
      Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return !claimsJws.getBody().getExpiration().before(new Date());
    }catch (JwtException | IllegalArgumentException e){
      throw new JwtAuthenticationException("JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
    }

  }

  public Authentication getAuthentication(String token){
    UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUsername(String token){
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
  }

  private static long toMilliseconds(long seconds, long minutes, long hours, long days){
    return (((days * 24 + hours) * 60 + minutes) * 60 + seconds) * 1000;
  }

  public String resolveToken(HttpServletRequest request){
    return request.getHeader(authorisationHeader);
  }

}
