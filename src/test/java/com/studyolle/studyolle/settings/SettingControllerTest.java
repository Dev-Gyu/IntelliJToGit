package com.studyolle.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.WithAccount;
import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.domain.Tag;
import com.studyolle.studyolle.service.AccountService;
import com.studyolle.studyolle.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    AccountService accountService;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @WithAccount("gyus")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    public void 태그수정폼() throws Exception{
        //given
        mockMvc.perform(get(SettingController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));

        //when


        //then

    }

    @WithAccount("gyus")
    @DisplayName("계정에 태그 추가")
    @Test
    public void 태그추가폼() throws Exception{
        //given
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");


        mockMvc.perform(post(SettingController.SETTINGS_TAGS_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account gyus = accountRepository.findByNickname("gyus");
        assertTrue(gyus.getTags().contains(newTag));
        //when


        //then

    }

    @WithAccount("gyus")
    @DisplayName("태그 삭제 폼")
    @Test
    public void 태그삭제폼() throws Exception{
        //given
        Account gyus = accountRepository.findByNickname("gyus");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(gyus, newTag);

        assertTrue(gyus.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingController.SETTINGS_TAGS_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(gyus.getTags().contains(newTag));

        //when


        //then

    }

    @WithAccount("gyus")
    @DisplayName("프로필 수정 하기 - 입력값 정상")
    @Test
    public void 프로파일_수정_폼() throws Exception{
        //given
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(get(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
        //when

        //then

    }

    @WithAccount("gyus")
    @DisplayName("프로필 수정 하기 - 입력값 정상")
    @Test
    public void 프로파일_수정() throws Exception{
        //given
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL)
                        .param("bio", "짧은 소개를 수정하는 경우.")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));
        //when

        Account gyus = accountRepository.findByNickname("gyus");
        assertEquals(bio,gyus.getBio());

        //then

    }

    @WithAccount("gyus")
    @DisplayName("프로필 수정 하기 - 에러")
    @Test
    public void 프로파일_수정_에러() throws Exception{
        //given
        String bio = "길게 소개를 하는 경우. 길게 소개를 수정하는 경우. 많이 길게 수정하는 경우많이 길게 수정하는 경우많이 길게 수정하는 경우많이 길게 수정하는 경우";
        mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("profile"));
        //when

        Account gyus = accountRepository.findByNickname("gyus");
        assertNull(gyus.getBio());

        //then

    }

}