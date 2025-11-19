package org.mpt.vet.clinic.services;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.mpt.vet.clinic.domains.*;
import org.mpt.vet.clinic.dto.CreateOwnerDto;
import org.mpt.vet.clinic.repositories.CatOwnerRepository;
import org.mpt.vet.clinic.repositories.CatRepository;
import org.mpt.vet.clinic.repositories.OwnerRepository;
import org.mpt.vet.clinic.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final CatRepository catRepository;
    private final CatOwnerRepository catOwnerRepository;
    private final UserService userService;

    @Transactional
    public void registerOwner(@NotNull CreateOwnerDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Пользователь с email '" + request.email() + "' уже существует.");
        }

        Role userRole = roleService.findByName(Role.RoleType.USER);

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(userRole));
        User savedUser = userRepository.save(user);

        Owner owner = Owner.builder()
                .fullName(request.fullName())
                .phone(request.phone())
                .address(request.address())
                .user(savedUser)
                .build();

        ownerRepository.save(owner);
    }

    @Transactional(readOnly = true)
    public Owner findByUserId(Long userId) {
        return ownerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Владелец не найден"));
    }

    @Transactional(readOnly = true)
    public List<Cat> findCatsByUserId(Long userId) {
        Owner owner = findByUserId(userId);
        return ownerRepository.findActiveCatsByOwnerId(owner.getId());
    }

    @Transactional(readOnly = true)
    public List<Cat> findOwnCatsByUserId(Long userId) {
        Owner owner = findByUserId(userId);
        return owner.getCatOwnerships().stream()
                .filter(catOwner -> catOwner.getOwnershipEndDate() == null)
                .map(CatOwner::getCat)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addCatToOwner(Cat cat, @NotNull Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.findByEmail(email);

        Owner owner = ownerRepository.findByUserId(user.getId()).orElseThrow(
                () -> new IllegalStateException("У вас нет профиля владельца. Заполните данные в профиле.")
        );

        Cat savedCat = catRepository.save(cat);

        CatOwner catOwner = CatOwner.builder()
                .cat(savedCat)
                .owner(owner)
                .ownershipStartDate(LocalDate.now())
                .build();
        catOwnerRepository.save(catOwner);
    }

    @Transactional
    public void updateOwnerInfo(Long ownerId, String fullName, String phone, String address) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Владелец не найден"));

        owner.setFullName(fullName);
        owner.setPhone(phone);
        owner.setAddress(address);

        ownerRepository.save(owner);
    }

    @Transactional(readOnly = true)
    public Owner getCurrentOwner(@NotNull Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.findByEmail(email);
        return findByUserId(user.getId());
    }
}
