package com.masterapply.backend.controller;

import static com.masterapply.backend.security.AuthUtil.estAdmin;
import static com.masterapply.backend.security.AuthUtil.uid;

import com.masterapply.backend.dto.RecuResponse;
import com.masterapply.backend.service.RecuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recus")
@RequiredArgsConstructor
public class RecuController {

    private final RecuService service;

    @GetMapping
    public List<RecuResponse> lister(@AuthenticationPrincipal Jwt jwt) {
        return service.lister(uid(jwt), estAdmin(jwt));
    }

    @GetMapping("/{id}")
    public RecuResponse trouver(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.trouver(id, uid(jwt), estAdmin(jwt));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> telecharger(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        RecuService.PdfFichier fichier = service.pdf(id, uid(jwt), estAdmin(jwt));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fichier.nom()).build().toString())
                .body(fichier.contenu());
    }
}