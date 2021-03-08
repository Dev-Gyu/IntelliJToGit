package com.studyolle.studyolle.modules.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class NicknameDto {
    @NotBlank
    @Length(min = 3, max = 20)
    private String nickname;
}
