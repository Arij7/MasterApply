package com.masterapply.backend.service;

import com.masterapply.backend.dto.CandidatureRequest;
import com.masterapply.backend.dto.CandidatureResponse;
import com.masterapply.backend.dto.DecisionRequest;
import com.masterapply.backend.dto.DocumentResponse;
import com.masterapply.backend.entity.Administrateur;
import com.masterapply.backend.entity.Campagne;
import com.masterapply.backend.entity.Candidat;
import com.masterapply.backend.entity.Candidature;
import com.masterapply.backend.entity.Document;
import com.masterapply.backend.entity.Master;
import com.masterapply.backend.exception.BadRequestException;
import com.masterapply.backend.exception.BusinessException;
import com.masterapply.backend.exception.ForbiddenException;
import com.masterapply.backend.exception.ResourceNotFoundException;
import com.masterapply.backend.repository.AdministrateurRepository;
import com.masterapply.backend.repository.CampagneRepository;
import com.masterapply.backend.repository.CandidatRepository;
import com.masterapply.backend.repository.CandidatureRepository;
import com.masterapply.backend.repository.MasterRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private static final List<String> TYPES_DOCUMENT = List.of(
            Document.CV, Document.RELEVE_NOTES, Document.DIPLOME,
            Document.PIECE_IDENTITE, Document.LETTRE_MOTIVATION);

    private static final Map<String, String> EXTENSIONS = Map.of(
            "application/pdf", ".pdf",
            "image/jpeg", ".jpg",
            "image/png", ".png");

    private final CandidatureRepository candidatureRepo;
    private final CandidatRepository candidatRepo;
    private final AdministrateurRepository adminRepo;
    private final MasterRepository masterRepo;
    private final CampagneRepository campagneRepo;

    @Value("#{'${app.documents.obligatoires}'.split(',')}")
    private List<String> documentsObligatoires;

    @Value("${app.upload.dir}")
    private String dossierUpload;

    // ------------------------------------------------------------------ candidat

    @Transactional
    public CandidatureResponse creer(Long candidatId, CandidatureRequest r) {
        Candidat candidat = candidatRepo.findById(candidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidat introuvable : id=" + candidatId));
        Master master = masterRepo.findById(r.masterId())
                .orElseThrow(() -> new ResourceNotFoundException("Master introuvable : id=" + r.masterId()));

        Campagne campagne;
        if (r.campagneId() != null) {
            campagne = campagneRepo.findById(r.campagneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campagne introuvable : id=" + r.campagneId()));
        } else {
            campagne = campagneRepo.findFirstByActiveTrueOrderByIdDesc()
                    .orElseThrow(() -> new BusinessException("Aucune campagne d'admission active"));
        }
        if (!campagne.isActive()) {
            throw new BusinessException("Cette campagne d'admission n'est pas active");
        }
        if (candidatureRepo.existsByCandidatIdAndCampagneIdAndMasterId(candidatId, campagne.getId(), master.getId())) {
            throw new BusinessException("Vous avez déjà une candidature pour ce Master dans cette campagne");
        }

        Candidature c = new Candidature();
        c.setCandidat(candidat);
        c.setMaster(master);
        c.setCampagne(campagne);
        c.setMotivation(r.motivation());
        return CandidatureResponse.from(candidatureRepo.save(c));
    }

    @Transactional
    public DocumentResponse ajouterDocument(Long id, Long candidatId, String type, MultipartFile fichier) {
        Candidature c = charger(id);
        verifierProprietaire(c, candidatId);
        if (!Candidature.BROUILLON.equals(c.getStatut())) {
            throw new BusinessException("Les documents ne peuvent être modifiés que sur un brouillon");
        }

        String typeNorm = type == null ? "" : type.trim().toUpperCase();
        if (!TYPES_DOCUMENT.contains(typeNorm)) {
            throw new BadRequestException("Type de document invalide. Valeurs possibles : "
                    + String.join(", ", TYPES_DOCUMENT));
        }
        if (fichier == null || fichier.isEmpty()) {
            throw new BadRequestException("Le fichier est vide");
        }
        String extension = EXTENSIONS.get(fichier.getContentType());
        if (extension == null) {
            throw new BadRequestException("Format non accepté : PDF, JPG ou PNG uniquement");
        }

        // un seul document par type : le nouveau remplace l'ancien
        c.getDocuments().removeIf(d -> {
            if (d.getType().equals(typeNorm)) {
                supprimerFichier(d.getChemin());
                return true;
            }
            return false;
        });

        Path destination = enregistrerFichier(id, fichier, extension);

        Document doc = new Document();
        doc.setCandidature(c);
        doc.setType(typeNorm);
        doc.setNomFichier(StringUtils.cleanPath(
                Objects.requireNonNullElse(fichier.getOriginalFilename(), "fichier" + extension)));
        doc.setChemin(destination.toString());
        c.getDocuments().add(doc);
        candidatureRepo.saveAndFlush(c);
        return DocumentResponse.from(doc);
    }

    @Transactional
    public CandidatureResponse soumettre(Long id, Long candidatId) {
        Candidature c = charger(id);
        verifierProprietaire(c, candidatId);

        if (!Candidature.BROUILLON.equals(c.getStatut())) {
            throw new BusinessException("Cette candidature a déjà été soumise (statut : " + c.getStatut() + ")");
        }
        if (!c.getCampagne().isActive()) {
            throw new BusinessException("La campagne d'admission est terminée");
        }

        // RG04 : documents obligatoires
        Set<String> presents = new HashSet<>();
        c.getDocuments().forEach(d -> presents.add(d.getType()));
        List<String> manquants = documentsObligatoires.stream()
                .map(String::trim)
                .filter(t -> !presents.contains(t))
                .toList();
        if (!manquants.isEmpty()) {
            throw new BusinessException("Documents obligatoires manquants : " + String.join(", ", manquants));
        }

        // RG02 : maximum de candidatures soumises par campagne
        int max = c.getCampagne().getMaxCandidatures();
        long dejaSoumises = candidatureRepo.countByCandidatIdAndCampagneIdAndStatutNot(
                candidatId, c.getCampagne().getId(), Candidature.BROUILLON);
        if (dejaSoumises >= max) {
            throw new BusinessException("Limite atteinte : vous ne pouvez pas soumettre plus de "
                    + max + " candidatures par campagne");
        }

        c.soumettre();
        return CandidatureResponse.from(candidatureRepo.save(c));
    }

    // ------------------------------------------------------------ consultation

    @Transactional(readOnly = true)
    public List<CandidatureResponse> lister(Long utilisateurId, boolean admin) {
        List<Candidature> liste = admin
                ? candidatureRepo.findAllByOrderByDateCreationDesc()
                : candidatureRepo.findByCandidatIdOrderByDateCreationDesc(utilisateurId);
        return liste.stream().map(CandidatureResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CandidatureResponse trouver(Long id, Long utilisateurId, boolean admin) {
        Candidature c = charger(id);
        verifierAcces(c, utilisateurId, admin);
        return CandidatureResponse.from(c);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> listerDocuments(Long id, Long utilisateurId, boolean admin) {
        Candidature c = charger(id);
        verifierAcces(c, utilisateurId, admin);
        return c.getDocuments().stream().map(DocumentResponse::from).toList();
    }

    // ----------------------------------------------------------- administrateur

    @Transactional
    public CandidatureResponse mettreEnExamen(Long id, Long adminId) {
        Candidature c = charger(id);
        c.mettreEnExamen(chargerAdmin(adminId));
        return CandidatureResponse.from(candidatureRepo.save(c));
    }

    @Transactional
    public CandidatureResponse decider(Long id, Long adminId, DecisionRequest r) {
        Candidature c = charger(id);
        Administrateur admin = chargerAdmin(adminId);
        if ("ACCEPTEE".equals(r.decision())) {
            c.accepter(admin);
        } else {
            c.refuser(admin);
        }
        c.setNoteDossier(r.noteDossier());
        c.setCommentaireAdmin(r.commentaire());
        return CandidatureResponse.from(candidatureRepo.save(c));
    }

    // ------------------------------------------------------------------ outils

    private Candidature charger(Long id) {
        return candidatureRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable : id=" + id));
    }

    private Administrateur chargerAdmin(Long adminId) {
        return adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Administrateur introuvable : id=" + adminId));
    }

    private void verifierAcces(Candidature c, Long utilisateurId, boolean admin) {
        if (!admin && !c.getCandidat().getId().equals(utilisateurId)) {
            throw new ForbiddenException("Accès refusé : cette candidature ne vous appartient pas");
        }
    }

    private void verifierProprietaire(Candidature c, Long candidatId) {
        if (!c.getCandidat().getId().equals(candidatId)) {
            throw new ForbiddenException("Accès refusé : cette candidature ne vous appartient pas");
        }
    }

    private Path enregistrerFichier(Long candidatureId, MultipartFile fichier, String extension) {
        try {
            Path dossier = Paths.get(dossierUpload, String.valueOf(candidatureId));
            Files.createDirectories(dossier);
            Path destination = dossier.resolve(UUID.randomUUID() + extension);
            Files.copy(fichier.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de l'enregistrement du fichier", e);
        }
    }

    private void supprimerFichier(String chemin) {
        try {
            if (chemin != null) {
                Files.deleteIfExists(Paths.get(chemin));
            }
        } catch (IOException ignored) {
            // le fichier ne gêne pas la suite
        }
    }
}