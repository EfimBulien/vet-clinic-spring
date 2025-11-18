package org.mpt.vet.clinic.repositories;


import org.mpt.vet.clinic.domains.CatOwner;
import org.mpt.vet.clinic.domains.CatOwnerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatOwnerRepository extends JpaRepository<CatOwner, CatOwnerId> {
    List<CatOwner> findByCatId(Long catId);
    boolean existsByCatIdAndOwnerId(Long catId, Long ownerId);
    Optional<CatOwner> findByCatIdAndOwnerId(Long catId, Long ownerId);
}
