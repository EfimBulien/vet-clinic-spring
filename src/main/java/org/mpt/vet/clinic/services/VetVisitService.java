package org.mpt.vet.clinic.services;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.mpt.vet.clinic.domains.VetVisit;
import org.mpt.vet.clinic.repositories.VetVisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VetVisitService {
    private final VetVisitRepository vetVisitRepository;
    private final CatOwnerService catOwnerService;

    public List<VetVisit> findByOwnerId(Long ownerId) {
        return vetVisitRepository.findByOwnerId(ownerId);
    }

    public VetVisit createVisit(@NotNull VetVisit vetVisit, Long ownerId) {
        boolean ownsCat = catOwnerService.existsByCatIdAndOwnerIdAndOwnershipEndDateIsNull(
                vetVisit.getCat().getId(), ownerId);

        if (!ownsCat) {
            throw new SecurityException("Эта кошка вам не принадлежит");
        }

        vetVisit.setDiagnosis(null);
        vetVisit.setTreatment(null);
        vetVisit.setCost(null);

        return vetVisitRepository.save(vetVisit);
    }

    public List<VetVisit> findPendingVisitsByOwnerId(Long ownerId) {
        return vetVisitRepository.findPendingVisitsByOwnerId(ownerId);
    }

    public List<VetVisit> findPendingVisits() {
        return vetVisitRepository.findByDiagnosisIsNullOrderByVisitDateAsc();
    }

    public VetVisit updateVisit(Long visitId, VetVisit vetVisit) {
        Optional<VetVisit> visit = vetVisitRepository.findById(visitId);
        return vetVisitRepository.save(vetVisit);
    }

    public VetVisit findById(Long id) {
        return vetVisitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit not found"));
    }
}
