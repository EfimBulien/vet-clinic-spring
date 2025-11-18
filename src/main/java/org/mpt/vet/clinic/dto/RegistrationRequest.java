package org.mpt.vet.clinic.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

public record RegistrationRequest(
        @NotBlank @Email @Setter @Getter String email,
        @NotBlank @Setter @Getter String password,
        @NotBlank @Setter @Getter String fullName,
        @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Некорректный телефон")
        @NotNull @Setter @Getter String phone,
        @Size(max = 512) @Setter @Getter String address
) {}