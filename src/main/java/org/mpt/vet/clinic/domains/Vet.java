package org.mpt.vet.clinic.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "vets", uniqueConstraints = @UniqueConstraint(columnNames = "phone"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 256)
    private String name;

    @Size(max = 256)
    private String specialty;

    @NotBlank
    @Size(max = 256)
    @Column(name = "clinic_name", nullable = false)
    private String clinicName;

    @Pattern(regexp = "\\+?\\d{10,20}")
    @Size(max = 20)
    @Column(unique = true)
    private String phone;
}