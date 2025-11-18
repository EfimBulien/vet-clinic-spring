package org.mpt.vet.clinic.domains;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    @Getter
    private RoleType name;

    public enum RoleType {
        USER,
        MANAGER,
        ADMIN
    }
}
