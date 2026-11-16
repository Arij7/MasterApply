package com.masterapply.backend.controller;

import static com.masterapply.backend.security.AuthUtil.estAdmin;
import static com.masterapply.backend.security.AuthUtil.uid;

import com.masterapply.backend.dto.PaiementRequest;
import com.masterapply.backend.dto.PaiementResponse;
import com.masterapply.backend.service.PaiementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService service;

    @PostMapping("/candidatures/{id}/paiements/simulation")
    @ResponseStatus(HttpStatus.CREATED)
    public PaiementResponse simuler(@AuthenticationPrincipal Jwt jwt,
                                    @PathVariable Long id,
                                    @RequestBody(required = false) PaiementRequest requete) {
        boolean forcerEchec = requete != null && Boolean.TRUE.equals(requete.forcerEchec());
        return service.simuler(id, uid(jwt), forcerEchec);
    }

    @GetMapping("/candidatures/{id}/paiements")
    public List<PaiementResponse> parCandidature(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.listerParCandidature(id, uid(jwt), estAdmin(jwt));
    }

    @GetMapping("/paiements")
    public List<PaiementResponse> tous() {
        return service.listerTous();
    }
}