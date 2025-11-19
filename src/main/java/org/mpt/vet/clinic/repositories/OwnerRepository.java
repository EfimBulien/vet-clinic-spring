package org.mpt.vet.clinic.repositories;

import org.mpt.vet.clinic.domains.Cat;
import org.mpt.vet.clinic.domains.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByUserId(Long userId);

    @Query("SELECT c FROM Cat c " +
            "JOIN c.ownerships co " +
            "WHERE co.owner.id = :ownerId AND co.ownershipEndDate IS NULL")
    List<Cat> findActiveCatsByOwnerId(@Param("ownerId") Long ownerId);
}
