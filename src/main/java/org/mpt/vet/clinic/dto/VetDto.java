package org.mpt.vet.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VetDto {
    @NotBlank(message = "Имя обязательно")
    @Size(max = 256, message = "Имя не более 256 символов")
    private String name;

    @Size(max = 256, message = "Специализация не более 256 символов")
    private String specialty;

    @NotBlank(message = "Название клиники обязательно")
    @Size(max = 256, message = "Название клиники не более 256 символов")
    private String clinicName;

    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Неверный формат телефона")
    @Size(max = 20, message = "Телефон не более 20 символов")
    private String phone;
}