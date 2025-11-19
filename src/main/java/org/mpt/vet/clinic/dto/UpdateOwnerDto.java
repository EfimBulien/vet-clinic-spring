package org.mpt.vet.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateOwnerDto {

    @NotBlank(message = "ФИО обязательно")
    @Size(max = 256, message = "ФИО не более 256 символов")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЁё\\s'\\-.]+$",
            message = "Допустимы только буквы, пробелы, дефисы, точки и апострофы"
    )
    private String fullName;

    @Pattern(
            regexp = "^\\+?[0-9]{10,20}$",
            message = "Допустимо + и 10–20 цифр (например: +79991234567)"
    )
    @Size(max = 20, message = "Телефон не более 20 символов")
    private String phone;

    @Size(max = 512, message = "Адрес не более 512 символов")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЁё0-9\\s,.'\\-/№]*$",
            message = "Допустимы только буквы, цифры, пробелы, запятые, точки, дефисы, /, №"
    )
    private String address;
}