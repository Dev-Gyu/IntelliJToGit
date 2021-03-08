package com.studyolle.studyolle.modules.main;

import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.SignupForm;
import com.studyolle.studyolle.modules.account.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach(){
        SignupForm signupForm = new SignupForm();
        signupForm.setNickname("gyus");
        signupForm.setEmail("flandre495@gamil.com");
        signupForm.setPassword("12345678");
        accountService.processNewAccount(signupForm);
    }
    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @Test
    public void 이메일_로그인_성공() throws Exception{
        //given

        mockMvc.perform(post("/login")
                .param("username", "flandre495@gamil.com")
                .param("password","12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("gyus"));

        //when


        //then

    }
    @Test
    public void 닉네임_로그인_성공() throws Exception{
        //given

        mockMvc.perform(post("/login")
                .param("username", "flandre495@gamil.com")
                .param("password","12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("gyus"));

        //when


        //then

    }
    @Test
    public void 로그인_실패() throws Exception{
        //given

        mockMvc.perform(post("/login")
                .param("username", "flandre495@gamil.com")
                .param("password","128")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

        //when


        //then

    }
    @DisplayName("로그아웃")
    @Test
    public void 로그아웃() throws Exception{
        //given
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());

        //when


        //then

    }

}