package com.masterapply.backend.service;

import com.masterapply.backend.dto.AuthResponse;
import com.masterapply.backend.dto.LoginRequest;
import com.masterapply.backend.dto.RegisterRequest;
import com.masterapply.backend.entity.Candidat;
import com.masterapply.backend.entity.Utilisateur;
import com.masterapply.backend.exception.BusinessException;
import com.masterapply.backend.repository.CandidatRepository;
import com.masterapply.backend.repository.UtilisateurRepository;
import com.masterapply.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepo;
    private final CandidatRepository candidatRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse inscrire(RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (utilisateurRepo.existsByEmail(email)) {
            throw new BusinessException("Un compte existe déjà avec cet email");
        }
        Candidat c = new Candidat();
        c.setNom(r.nom().trim());
        c.setPrenom(r.prenom().trim());
        c.setEmail(email);
        c.setMotDePasse(encoder.encode(r.motDePasse()));
        c.setRole(Utilisateur.ROLE_CANDIDAT);
        c.setDateNaissance(r.dateNaissance());
        c.setTelephone(r.telephone());
        c.setNationalite(r.nationalite());
        c.setMoyenneGenerale(r.moyenneGenerale());
        candidatRepo.save(c);
        return reponse(c);
    }

    @Transactional(readOnly = true)
    public AuthResponse connecter(LoginRequest r) {
        Utilisateur u = utilisateurRepo.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.motDePasse(), x.getMotDePasse()))
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));
        return reponse(u);
    }

    private AuthResponse reponse(Utilisateur u) {
        return new AuthResponse(jwtService.generer(u), "Bearer", u.getId(),
                u.getNom(), u.getPrenom(), u.getEmail(), u.getRole());
    }
}