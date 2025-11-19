package org.mpt.vet.clinic.repositories;

import org.mpt.vet.clinic.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT o.user.id FROM Owner o WHERE o.user IS NOT NULL)")
    List<User> findUsersWithoutOwner();
}
