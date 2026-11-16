package com.masterapply.backend.controller;

import com.masterapply.backend.entity.Master;
import com.masterapply.backend.service.MasterService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/masters")
@RequiredArgsConstructor
public class MasterController {

    private final MasterService service;

    @GetMapping
    public List<Master> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    public Master trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Master creer(@Valid @RequestBody Master m) {
        return service.creer(m);
    }

    @PutMapping("/{id}")
    public Master modifier(@PathVariable Long id, @Valid @RequestBody Master m) {
        return service.modifier(id, m);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}