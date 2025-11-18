package org.mpt.vet.clinic.domains;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CatOwnerId implements Serializable {
    private Long cat;
    private Long owner;
}
