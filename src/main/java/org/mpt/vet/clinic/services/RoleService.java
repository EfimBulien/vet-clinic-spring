package org.mpt.vet.clinic.services;

import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.Role;
import org.mpt.vet.clinic.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findByName(Role.RoleType name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Роль " + name + " не найдена в БД"));
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}