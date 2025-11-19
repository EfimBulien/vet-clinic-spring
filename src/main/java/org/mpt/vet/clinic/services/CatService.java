package org.mpt.vet.clinic.services;

import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.Cat;
import org.mpt.vet.clinic.domains.CatOwner;
import org.mpt.vet.clinic.repositories.CatOwnerRepository;
import org.mpt.vet.clinic.repositories.CatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatService {
    private final CatRepository catRepository;
    private final CatOwnerRepository catOwnerRepository;

    public List<Cat> findAll() {
        return catRepository.findAll();
    }

    public Cat findById(Long id) {
        return catRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Кошка не найдена")
        );
    }

    public Cat save(Cat cat) {
        return catRepository.save(cat);
    }

    public void deleteById(Long id, boolean force) {
        if (!force) {
            List<CatOwner> links = catOwnerRepository.findByCatId(id);
            if (!links.isEmpty()) {
                throw new IllegalStateException("Кошка имеет владельцев. Используйте force=true.");
            }
        }
        catRepository.deleteById(id);
    }

    public boolean hasOwners(Long catId) {
        return !catOwnerRepository.findByCatId(catId).isEmpty();
    }
}
