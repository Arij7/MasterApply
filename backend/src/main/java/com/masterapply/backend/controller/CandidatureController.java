package com.masterapply.backend.controller;

import com.masterapply.backend.dto.CandidatureRequest;
import com.masterapply.backend.dto.CandidatureResponse;
import com.masterapply.backend.dto.DecisionRequest;
import com.masterapply.backend.dto.DocumentResponse;
import com.masterapply.backend.service.CandidatureService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CandidatureResponse creer(@AuthenticationPrincipal Jwt jwt,
                                     @Valid @RequestBody CandidatureRequest r) {
        return service.creer(uid(jwt), r);
    }

    @GetMapping
    public List<CandidatureResponse> lister(@AuthenticationPrincipal Jwt jwt) {
        return service.lister(uid(jwt), estAdmin(jwt));
    }

    @GetMapping("/{id}")
    public CandidatureResponse trouver(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.trouver(id, uid(jwt), estAdmin(jwt));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse ajouterDocument(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long id,
                                            @RequestParam String type,
                                            @RequestParam("file") MultipartFile file) {
        return service.ajouterDocument(id, uid(jwt), type, file);
    }

    @GetMapping("/{id}/documents")
    public List<DocumentResponse> documents(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.listerDocuments(id, uid(jwt), estAdmin(jwt));
    }

    @PatchMapping("/{id}/soumettre")
    public CandidatureResponse soumettre(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.soumettre(id, uid(jwt));
    }

    @PatchMapping("/{id}/examen")
    public CandidatureResponse examen(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.mettreEnExamen(id, uid(jwt));
    }

    @PatchMapping("/{id}/decision")
    public CandidatureResponse decision(@AuthenticationPrincipal Jwt jwt,
                                        @PathVariable Long id,
                                        @Valid @RequestBody DecisionRequest r) {
        return service.decider(id, uid(jwt), r);
    }

    private static Long uid(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    private static boolean estAdmin(Jwt jwt) {
        return "ADMIN".equals(jwt.getClaimAsString("role"));
    }
}