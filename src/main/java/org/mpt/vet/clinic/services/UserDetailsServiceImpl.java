package org.mpt.vet.clinic.services;

import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.User;
import org.mpt.vet.clinic.models.UserPrincipal;
import org.mpt.vet.clinic.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь не найден: " + email)
        );
        return UserPrincipal.build(user);
    }
}
