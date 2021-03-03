package com.studyolle.studyolle;

import com.studyolle.studyolle.account.SignupForm;
import com.studyolle.studyolle.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String nickname = withAccount.value();

        SignupForm signupForm = new SignupForm();
        signupForm.setNickname(nickname);
        signupForm.setEmail("flandre495@gamil.com");
        signupForm.setPassword("12345678");

        accountService.processNewAccount(signupForm);

        UserDetails principal = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        return context;
    }
}
