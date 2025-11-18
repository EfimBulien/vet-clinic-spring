package org.mpt.vet.clinic.repositories;

import org.mpt.vet.clinic.domains.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long> {

}
