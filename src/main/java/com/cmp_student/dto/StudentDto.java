package com.cmp_student.dto;

import com.cmp_student.constraints.SanitizedHtml;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import tools.jackson.databind.ObjectMapper;

public record StudentDto(
        @JsonIgnore Integer id,
        @NotBlank(message = "Name no debe estar vacío") String name,
        @SanitizedHtml(message = "Last name contiene código malicioso") String lastName,
        @SanitizedHtml(message = "Potentially dangerous HTML code was detected in the biography.") String phone,
        String eMail) {

    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

}
