package com.studyolle.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SignupForm signupForm = (SignupForm) o;
        if(accountRepository.existsByEmail(signupForm.getEmail())){
            errors.rejectValue("email","invalid.email", new Object[]{signupForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }
        if(accountRepository.existsByNickname(signupForm.getNickname())){
            errors.rejectValue("nickname","invalid.nickname", new Object[]{signupForm.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
