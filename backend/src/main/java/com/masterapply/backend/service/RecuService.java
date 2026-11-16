package com.masterapply.backend.service;

import com.masterapply.backend.dto.RecuResponse;
import com.masterapply.backend.entity.Paiement;
import com.masterapply.backend.entity.Recu;
import com.masterapply.backend.exception.ForbiddenException;
import com.masterapply.backend.exception.ResourceNotFoundException;
import com.masterapply.backend.repository.RecuRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecuService {

    public record PdfFichier(String nom, byte[] contenu) {
    }

    private final RecuRepository recuRepo;
    private final PdfRecuGenerator generateur;

    @Value("${app.upload.dir}")
    private String dossierUpload;

    /** Appelé uniquement quand le paiement est REUSSI (RG08). */
    @Transactional
    public Recu creerPourPaiement(Paiement paiement) {
        Recu recu = new Recu();
        recu.setPaiement(paiement);
        recu.setNumero("REC-" + Year.now().getValue() + "-" + String.format("%06d", paiement.getId()));
        recu = recuRepo.saveAndFlush(recu);

        Path fichier = ecrirePdf(recu);
        recu.setCheminPdf(fichier.toString());
        return recuRepo.save(recu);
    }

    @Transactional(readOnly = true)
    public List<RecuResponse> lister(Long utilisateurId, boolean admin) {
        List<Recu> liste = admin
                ? recuRepo.findAllByOrderByDateGenerationDesc()
                : recuRepo.findByPaiementCandidatureCandidatIdOrderByDateGenerationDesc(utilisateurId);
        return liste.stream().map(RecuResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RecuResponse trouver(Long id, Long utilisateurId, boolean admin) {
        Recu recu = charger(id);
        verifierAcces(recu, utilisateurId, admin);
        return RecuResponse.from(recu);
    }

    @Transactional(readOnly = true)
    public PdfFichier pdf(Long id, Long utilisateurId, boolean admin) {
        Recu recu = charger(id);
        verifierAcces(recu, utilisateurId, admin);
        try {
            Path chemin = recu.getCheminPdf() == null ? null : Paths.get(recu.getCheminPdf());
            byte[] contenu = (chemin != null && Files.exists(chemin))
                    ? Files.readAllBytes(chemin)
                    : generateur.generer(recu);
            return new PdfFichier(recu.getNumero() + ".pdf", contenu);
        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de la lecture du reçu PDF", e);
        }
    }

    private Recu charger(Long id) {
        return recuRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reçu introuvable : id=" + id));
    }

    private void verifierAcces(Recu recu, Long utilisateurId, boolean admin) {
        Long proprietaire = recu.getPaiement().getCandidature().getCandidat().getId();
        if (!admin && !proprietaire.equals(utilisateurId)) {
            throw new ForbiddenException("Accès refusé : ce reçu ne vous appartient pas");
        }
    }

    private Path ecrirePdf(Recu recu) {
        try {
            Path dossier = Paths.get(dossierUpload, "recus");
            Files.createDirectories(dossier);
            Path fichier = dossier.resolve(recu.getNumero() + ".pdf");
            Files.write(fichier, generateur.generer(recu));
            return fichier;
        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de l'enregistrement du reçu PDF", e);
        }
    }
}