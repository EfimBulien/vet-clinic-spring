package org.mpt.vet.clinic.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateRolesDto {
    private List<String> roles;
}
