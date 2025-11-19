package org.mpt.vet.clinic.services;

import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.CatOwner;
import org.mpt.vet.clinic.repositories.CatOwnerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CatOwnerService {
    private final CatOwnerRepository catOwnerRepository;

    public void save(CatOwner catOwner) {
        catOwnerRepository.save(catOwner);
    }

    public boolean existsByCatIdAndOwnerId(Long catId, Long ownerId) {
        return catOwnerRepository.existsByCatIdAndOwnerId(catId, ownerId);
    }

    public Optional<CatOwner> findByCatIdAndOwnerId(Long catId, Long ownerId) {
        return catOwnerRepository.findByCatIdAndOwnerId(catId, ownerId);
    }

    public boolean existsByCatIdAndOwnerIdAndOwnershipEndDateIsNull(Long catId, Long ownerId) {
        return catOwnerRepository.existsByCatIdAndOwnerIdAndOwnershipEndDateIsNull(catId, ownerId);
    }
}
