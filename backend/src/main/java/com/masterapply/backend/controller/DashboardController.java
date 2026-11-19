package com.masterapply.backend.controller;

import com.masterapply.backend.dto.DashboardResponse;
import com.masterapply.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/statistiques")
    public DashboardResponse statistiques() {
        return service.calculer();
    }
}