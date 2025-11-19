package org.mpt.vet.clinic.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateVetVisitDto {

    @NotNull(message = "Выберите кошку")
    private Long catId;

    @NotNull(message = "Выберите ветеринара")
    private Long vetId;

    @NotNull(message = "Выберите дату визита")
    @FutureOrPresent(message = "Дата визита не может быть в прошлом")
    private LocalDate visitDate;
}