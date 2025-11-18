package org.mpt.vet.clinic.repositories;

import org.mpt.vet.clinic.domains.Vet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VetRepository extends JpaRepository<Vet, Long> {
    boolean existsByPhone(String phone);
    boolean existsByNameAndSpecialtyAndClinicName(String name, String specialty, String clinicName);
    boolean existsByNameAndSpecialtyAndClinicNameAndIdNot(String name, String specialty, String clinicName, Long id);
}
