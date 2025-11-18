package org.mpt.vet.clinic.services;

import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.VetVisit;
import org.mpt.vet.clinic.repositories.VetVisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VetVisitService {
    private final VetVisitRepository vetVisitRepository;

    public List<VetVisit> findByOwnerId(Long ownerId) {
        return vetVisitRepository.findByOwnerId(ownerId);
    }
}
