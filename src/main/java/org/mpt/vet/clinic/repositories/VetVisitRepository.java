package org.mpt.vet.clinic.repositories;

import org.mpt.vet.clinic.domains.VetVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VetVisitRepository extends JpaRepository<VetVisit, Long> {
    @Query("SELECT vv FROM VetVisit vv " +
            "JOIN vv.cat c " +
            "JOIN c.ownerships co " +
            "JOIN co.owner o " +
            "WHERE o.id = :ownerId AND co.ownershipEndDate IS NULL " +
            "ORDER BY vv.visitDate DESC")
    List<VetVisit> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT vv FROM VetVisit vv " +
            "JOIN vv.cat c " +
            "JOIN c.ownerships co " +
            "JOIN co.owner o " +
            "JOIN o.user u " +
            "WHERE u.id = :userId AND co.ownershipEndDate IS NULL " +
            "ORDER BY vv.visitDate DESC")
    List<VetVisit> findByUserId(@Param("userId") Long userId);

    @Query("SELECT vv FROM VetVisit vv " +
            "JOIN vv.cat c " +
            "JOIN c.ownerships co " +
            "WHERE co.owner.id = :ownerId AND co.ownershipEndDate IS NULL " +
            "AND vv.diagnosis IS NULL " +
            "ORDER BY vv.visitDate ASC")
    List<VetVisit> findPendingVisitsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT vv FROM VetVisit vv WHERE vv.diagnosis IS NULL ORDER BY vv.visitDate ASC")
    List<VetVisit> findByDiagnosisIsNullOrderByVisitDateAsc();
}
