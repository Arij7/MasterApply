package com.masterapply.backend.service;

import com.masterapply.backend.entity.Master;
import com.masterapply.backend.exception.BusinessException;
import com.masterapply.backend.exception.ResourceNotFoundException;
import com.masterapply.backend.repository.MasterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasterService {

    private final MasterRepository repo;

    public List<Master> lister() {
        return repo.findAll();
    }

    public Master trouver(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Master introuvable : id=" + id));
    }

    public Master creer(Master m) {
        m.setId(null);
        return repo.save(m);
    }

    public Master modifier(Long id, Master donnees) {
        Master m = trouver(id);
        m.setIntitule(donnees.getIntitule());
        m.setDescription(donnees.getDescription());
        m.setConditions(donnees.getConditions());
        m.setCapacite(donnees.getCapacite());
        m.setFrais(donnees.getFrais());
        return repo.save(m);
    }

    public void supprimer(Long id) {
        Master m = trouver(id);
        try {
            repo.delete(m);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(
                "Suppression impossible : ce Master est déjà utilisé par des candidatures");
        }
    }
}