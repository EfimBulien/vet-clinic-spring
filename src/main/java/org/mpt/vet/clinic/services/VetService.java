package org.mpt.vet.clinic.services;

import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.Vet;
import org.mpt.vet.clinic.repositories.VetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VetService {
    public final VetRepository vetRepository;

    public List<Vet> findAll() {
        return vetRepository.findAll();
    }

    public Vet findById(Long id) {
        return vetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vet not found"));
    }

    public Vet save(Vet vet) {
        return vetRepository.save(vet);
    }

    public void deleteById(Long id) {
        vetRepository.deleteById(id);
    }

    public boolean existsByNameAndSpecialtyAndClinicName(
            String name,
            String specialty,
            String clinicName) {
        return vetRepository.existsByNameAndSpecialtyAndClinicName(name, specialty, clinicName);
    }

    public boolean existsByNameAndSpecialtyAndClinicNameAndIdNot(
            String name,
            String specialty,
            String clinicName,
            Long id) {
        return vetRepository.existsByNameAndSpecialtyAndClinicNameAndIdNot(name, specialty, clinicName, id);
    }

    public boolean existsByPhone(String phone) {
        return vetRepository.existsByPhone(phone);
    }
}
