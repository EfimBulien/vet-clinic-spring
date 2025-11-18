package org.mpt.vet.clinic.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vet_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VetVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "vet_id", nullable = false)
    private Vet vet;

    @NotNull
    @PastOrPresent(message = "Дата визита не может быть в будущем")
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate = LocalDate.now();

    private String diagnosis;
    private String treatment;

    @DecimalMin(value = "0.0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal cost;
}