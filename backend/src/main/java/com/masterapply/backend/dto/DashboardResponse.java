package com.masterapply.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        long totalCandidatures,
        long brouillons,
        long enAttente,
        long acceptees,
        long refusees,
        long paiementsReussis,
        long inscriptionsConfirmees,
        List<StatMaster> parMaster,
        List<StatJour> parJour
) {
    public record StatMaster(String master, long total) {
    }

    public record StatJour(LocalDate date, long total) {
    }
}