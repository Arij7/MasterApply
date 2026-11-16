package com.masterapply.backend.controller;

import static com.masterapply.backend.security.AuthUtil.estAdmin;
import static com.masterapply.backend.security.AuthUtil.uid;

import com.masterapply.backend.dto.InscriptionResponse;
import com.masterapply.backend.service.InscriptionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService service;

    @PostMapping("/candidatures/{id}/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public InscriptionResponse confirmer(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.confirmer(id, uid(jwt));
    }

    @GetMapping("/inscriptions")
    public List<InscriptionResponse> lister(@AuthenticationPrincipal Jwt jwt) {
        return service.lister(uid(jwt), estAdmin(jwt));
    }
}