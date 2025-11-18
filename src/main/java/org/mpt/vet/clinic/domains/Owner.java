package org.mpt.vet.clinic.domains;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.validation.constraints.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "owners", uniqueConstraints = {
        @UniqueConstraint(columnNames = "phone"),
        @UniqueConstraint(columnNames = "email")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Собственный ID
    private Long id;

    @NotBlank(message = "ФИО обязательно")
    @Size(max = 256, message = "ФИО не более 256 символов")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЁё\\s'\\-.]+$",
            message = "Допустимо только буквы, пробелы, дефисы, точки и апострофы"
    )
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Pattern(
            regexp = "^\\+?[0-9]{10,20}$",
            message = "Допустимо + и 10–20 цифр (например: +79991234567)"
    )
    @Size(max = 20, message = "Телефон не более 20 символов")
    @Column(unique = true)
    private String phone;

    @Size(max = 512, message = "Адрес не более 512 символов")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЁё0-9\\s,.'\\-/№]+$",
            message = "Допустимо только буквы, цифры, пробелы, запятые, точки, дефисы, /, №"
    )
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;  // Убрать @MapsId

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CatOwner> catOwnerships = new ArrayList<>();
}