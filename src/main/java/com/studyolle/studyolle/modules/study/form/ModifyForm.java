package com.studyolle.studyolle.modules.study.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ModifyForm {
    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9_-]{2,20}$")
    private String path;

//    @NotBlank
    @Length(max = 50)
    private String title;
}
