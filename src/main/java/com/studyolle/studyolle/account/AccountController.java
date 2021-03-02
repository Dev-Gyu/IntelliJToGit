package com.studyolle.studyolle.account;

import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String sign_up(@ModelAttribute("signUpForm") SignupForm signupForm){
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute("signUpForm") SignupForm signupForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }
        Account account = accountService.processNewAccount(signupForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if(account == null){
            model.addAttribute("error", "wrong.email");
            return view;
        }
        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong.email");
            return view;
        }

        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname",account.getNickname());

        return view;
    }

}