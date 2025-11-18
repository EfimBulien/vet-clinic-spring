package org.mpt.vet.clinic.models;

import org.jetbrains.annotations.NotNull;
import org.mpt.vet.clinic.domains.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public record UserPrincipal(Long id, String email, String password,
                            Collection<? extends GrantedAuthority> authorities) implements UserDetails {

    public static @NotNull UserPrincipal build(@NotNull User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    String roleDesignation = role.getName().name();
                    return new SimpleGrantedAuthority("ROLE_" + roleDesignation);
                })
                .collect(toList());
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
