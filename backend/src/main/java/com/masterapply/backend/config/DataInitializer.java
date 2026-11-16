package com.masterapply.backend.config;

import com.masterapply.backend.entity.Administrateur;
import com.masterapply.backend.entity.Campagne;
import com.masterapply.backend.entity.Utilisateur;
import com.masterapply.backend.repository.AdministrateurRepository;
import com.masterapply.backend.repository.CampagneRepository;
import com.masterapply.backend.repository.UtilisateurRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CampagneRepository campagneRepo;
    private final AdministrateurRepository adminRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (campagneRepo.count() == 0) {
            Campagne c = new Campagne();
            c.setLibelle("Campagne 2026-2027");
            c.setDateDebut(LocalDate.of(2026, 11, 1));
            c.setDateFin(LocalDate.of(2027, 2, 28));
            campagneRepo.save(c);
        }
        if (!utilisateurRepo.existsByEmail("admin@masterapply.com")) {
            Administrateur a = new Administrateur();
            a.setNom("Admin");
            a.setPrenom("MasterApply");
            a.setEmail("admin@masterapply.com");
            a.setMotDePasse(encoder.encode("Admin12345"));
            a.setRole(Utilisateur.ROLE_ADMIN);
            a.setMatricule("ADM-001");
            adminRepo.save(a);
        }
    }
}