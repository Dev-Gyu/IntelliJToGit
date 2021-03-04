package com.studyolle.studyolle.main;

import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(account != null){
            model.addAttribute("account", account);
        }
        return "index";
    }
    @GetMapping("/login")
    public String login(){
        return "login/login";
    }

    @GetMapping("/email-login")
    public String email_login(@ModelAttribute EmailLoginDto emailLoginDto , Model model){
        return "login/email-login";
    }

    @PostMapping("/email-login")
    public String email_login_do(@CurrentUser Account account,@Valid @ModelAttribute EmailLoginDto emailLoginDto ,
                                 BindingResult result , Model model, RedirectAttributes attributes){
        if(result.hasErrors()){
            return "login/email-login";
        }
        accountService.emailTokenSend(account, emailLoginDto,result);
        if(result.hasErrors()){
            return "login/email-login";
        }
        attributes.addFlashAttribute("message", "이메일을 발송 하였습니다.");
        return "account/login-by-email";
    }

    @GetMapping("login-by-email")
    public String loginByEmail(@RequestParam String token, @RequestParam String email, Model model){
        Account findAccount = accountRepository.findByEmail(email);
        if(findAccount == null || !findAccount.isValidToken(token)){
            model.addAttribute("errors", "로그인 할 수 없습니다.");
            return "login/email-login";
        }
        accountService.login(findAccount);
        return "account/login-by-email";
    }
}
