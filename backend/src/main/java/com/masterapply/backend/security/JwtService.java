package com.masterapply.backend.security;

import com.masterapply.backend.entity.Utilisateur;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expirationMs;

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.encoder = encoder;
        this.expirationMs = expirationMs;
    }

    public String generer(Utilisateur u) {
        Instant maintenant = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(u.getEmail())
                .issuedAt(maintenant)
                .expiresAt(maintenant.plusMillis(expirationMs))
                .claim("role", u.getRole())
                .claim("uid", u.getId())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}