package com.example.nvt.service;


import com.example.nvt.model.SuperAdmin;
import com.example.nvt.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String SECRET_KEY = "73b65149d2e77ab667711a01e6e68a89a75e8e506faead08c8c3f50d80a00d38";
    private final UserRepository userRepository;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, Long id){

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){

        if("SUPERADMIN".equals(userDetails.getAuthorities().iterator().next().getAuthority())){
            var user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow();
            if(user instanceof SuperAdmin superAdmin){
                if(superAdmin.isFirstLogin()) extraClaims.put("isFirstLogin", true);
                else extraClaims.put("isFirstLogin", false);

            }
        }

        extraClaims.put("role", userDetails.getAuthorities());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){

        String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public Boolean extractIsFirstLogin(String token) {

        Claims claims = extractAllClaims(token);
        return claims.containsKey("isFirstLogin") ? (Boolean) claims.get("isFirstLogin") : null;
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.containsKey("role") ? claims.get("role").toString() : null;
    }

    public Boolean isSuperAdminPasswordChanged(String token){
        if(!extractRole(token).equals("SUPERADMIN")) return null;

        return extractIsFirstLogin(token);
    }



    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}