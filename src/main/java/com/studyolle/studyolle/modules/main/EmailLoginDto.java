package com.studyolle.studyolle.modules.main;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EmailLoginDto {

    @NotBlank
    @Email
    private String email;
}
