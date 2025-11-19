package org.mpt.vet.clinic.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "cats"
)
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЁё\\s'\\-]+$",
            message = "Допустимы только буквы, пробелы, дефисы и апострофы"
    )
    @Column(nullable = false)
    @Size(min = 3, max = 256, message = "Имя должно быть от 3 символов и не более 256 символов")
    private String name;

    @NotNull(message = "Возраст обязателен")
    @Min(value = 1, message = "Возраст должен быть >= 1")
    @Max(value = 30, message = "Возраст не более 30 лет")
    @Builder.Default
    private Integer age = 1;

    @Size(min = 3, max = 256, message = "Порода должна быть от 3 не более 256 символов")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЁё\\s'\\-]*$",
            message = "Допустимы только буквы, пробелы, дефисы и апострофы"
    )
    private String breed;

    @OneToMany(mappedBy = "cat", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CatOwner> ownerships = new ArrayList<>();
}
