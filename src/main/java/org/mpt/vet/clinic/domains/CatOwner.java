package org.mpt.vet.clinic.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cat_owners")
@IdClass(CatOwnerId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatOwner {

    @Id
    @ManyToOne
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @Id
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @NotNull
    @Column(name = "ownership_start_date")
    private LocalDate ownershipStartDate = LocalDate.now();

    @Column(name = "ownership_end_date")
    private LocalDate ownershipEndDate;

    @PrePersist @PreUpdate
    private void validateDates() {
        if (ownershipEndDate != null && ownershipEndDate.isBefore(ownershipStartDate)) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }
    }
}