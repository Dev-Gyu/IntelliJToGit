package com.studyolle.studyolle.settings.validator;

import com.studyolle.studyolle.settings.NicknameDto;
import org.apache.catalina.mapper.Mapper;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NicknameValidator implements Validator {
    private String regExp = "^[ᄀ-ᄒ가-힣a-z0-9_-]{3,20}$";
    private Pattern pattern;

    @Override
    public boolean supports(Class<?> aClass) {
        pattern = Pattern.compile(regExp);
        return NicknameDto.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameDto nicknameDto = (NicknameDto) target;
        Matcher matcher = pattern.matcher(nicknameDto.getNickname());
        if(!matcher.matches()){
            errors.rejectValue("nickname", null, "올바른 닉네임을 입력해주세요");
        }

    }
}
