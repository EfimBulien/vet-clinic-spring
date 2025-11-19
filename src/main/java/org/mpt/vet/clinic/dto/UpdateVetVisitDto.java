package org.mpt.vet.clinic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateVetVisitDto {

    @NotBlank(message = "Диагноз обязателен")
    private String diagnosis;

    @NotBlank(message = "Лечение обязателен")
    private String treatment;

    @DecimalMin(value = "0.0", message = "Стоимость не может быть отрицательной")
    @Digits(integer = 10, fraction = 2, message = "Неверный формат стоимости")
    private BigDecimal cost;
}
