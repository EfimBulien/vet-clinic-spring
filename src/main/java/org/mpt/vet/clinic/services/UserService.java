package org.mpt.vet.clinic.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.mpt.vet.clinic.domains.Role;
import org.mpt.vet.clinic.domains.User;
import org.mpt.vet.clinic.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void registerUser(@NotNull User user, Role.RoleType defaultRole) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Такой Email уже есть!");
        }

        Role role = roleService.findByName(defaultRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(role);

        userRepository.save(user);
    }

    @Transactional
    public void registerUser(@NotNull User user, @NotEmpty Set<Role.RoleType> roles) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email уже занят: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roleEntities = roles.stream()
                .map(roleService::findByName)
                .collect(Collectors.toSet());

        user.getRoles().clear();
        user.getRoles().addAll(roleEntities);

        userRepository.save(user);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteById(@NotNull Long id) {
        if (id.equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            throw new IllegalArgumentException("Нельзя удалить себя");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateRoles(Long userId, @NotNull Set<Role.RoleType> newRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Set<Role> roles = newRoles.stream()
                .map(roleService::findByName)
                .collect(Collectors.toSet());

        user.getRoles().clear();
        user.getRoles().addAll(roles);
        userRepository.save(user);
    }
}
