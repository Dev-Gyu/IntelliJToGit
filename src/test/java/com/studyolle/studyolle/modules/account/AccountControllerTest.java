package com.studyolle.studyolle.modules.account;

import com.studyolle.studyolle.infra.mail.ConsoleMailSender;
import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private AccountRepository accountRepository;

    @MockBean
    ConsoleMailSender consoleMailSender;

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    public void checkEmailToken_with_worng_input() throws Exception{
        //given
        mockMvc.perform(get("/check-email-token")
                        .param("token", "asdfasdfasdf")
                        .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());

        //when


        //then

    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    public void checkEmailToken_with_correct_input() throws Exception{
        Account account = Account.builder()
                .email("test@gmail.com")
                .password("12345678")
                .nickname("gyus")
                .build();
        Account savedAccount = accountRepository.save(account);
        savedAccount.generateToken();

        //given
        mockMvc.perform(get("/check-email-token")
                .param("token", savedAccount.getEmailCheckToken())
                .param("email", savedAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("gyus"));

        //when


        //then

    }

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    public void signupForm() throws Exception{
        //given
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());

        //when


        //then

    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    public void signUpSubmit_with_wrong_input() throws Exception{
        //given
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "gyus")
                        .param("email", "email..")
                        .param("password", "123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());

        //when


        //then

    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    public void signUpSubmit_with_correct_input() throws Exception{
        //given
        mockMvc.perform(post("/sign-up")
                .param("nickname", "gyus")
                .param("email", "flandre495@naver.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("gyus"));

        Account findAccount = accountRepository.findByEmail("flandre495@naver.com");
        assertNotNull(findAccount);
        assertNotNull(findAccount.getEmailCheckToken());
        assertNotEquals(findAccount.getPassword(), "12345678");

        assertTrue(accountRepository.existsByEmail("flandre495@naver.com"));
        // consoleMailSender에서 send를 어떠한 SimpleMailMessage인스턴스타입이든 상관없이 실행했는지 보는것
        then(consoleMailSender).should().send(any(SimpleMailMessage.class));

        //when


        //then

    }
}