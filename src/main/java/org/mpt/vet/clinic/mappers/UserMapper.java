package org.mpt.vet.clinic.mappers;

import org.mpt.vet.clinic.domains.User;
import org.mpt.vet.clinic.dto.RegistrationDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(RegistrationDto request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());

        return user;
    }
}
