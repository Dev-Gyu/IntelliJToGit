package com.studyolle.studyolle.modules.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.modules.account.CurrentUser;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.tag.Tag;
import com.studyolle.studyolle.modules.zone.Zone;
import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.modules.settings.validator.NicknameValidator;
import com.studyolle.studyolle.modules.settings.validator.PasswordFormValidator;
import com.studyolle.studyolle.modules.tag.TagRepository;
import com.studyolle.studyolle.modules.zone.ZoneDto;
import com.studyolle.studyolle.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/notification";
    static final String SETTINGS_NOTIFICATION_URL = "/settings/notifications";

    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/settings/account";

    static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    static final String SETTINGS_TAGS_URL = "/settings/tags";

    static final String SETTINGS_ZONES_VIEW_NAME = "settings/zones";
    static final String SETTINGS_ZONES_URL = "/settings/zones";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameDto")
    public void initBinder2(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new NicknameValidator());
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS_PROFILE_VIEW_NAME;
    }
    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }
    @GetMapping(SETTINGS_PASSWORD_VIEW_NAME)
    public String changePassword(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid @ModelAttribute PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }
        accountService.updatePassword(account, passwordForm);
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    public String notification(@CurrentUser Account account, Model model){
        model.addAttribute(modelMapper.map(account, NotificationForm.class));
        model.addAttribute(account);
        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATION_URL)
    public String notification_do(@CurrentUser Account account,@ModelAttribute NotificationForm notificationForm, Errors errors,
                                  Model model, RedirectAttributes attributes){
        accountService.updateNotification(account, notificationForm);
        attributes.addFlashAttribute("message", "수정 완료 되었습니다.");
        model.addAttribute(account);
        return "redirect:" + SETTINGS_NOTIFICATION_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String account(@CurrentUser Account account, @ModelAttribute NicknameDto nicknameDto, Model model){
        model.addAttribute(account);
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String account_do(@CurrentUser Account account, @Valid @ModelAttribute NicknameDto nicknameDto, BindingResult result,
                          Model model, RedirectAttributes attributes){
        if(result.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }
        accountService.updateNickname(account, nicknameDto);
        model.addAttribute(account);
        attributes.addFlashAttribute("message", "수정완료");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }
    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(t -> t.getTitle()).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(t -> t.getTitle()).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        return SETTINGS_TAGS_VIEW_NAME;
    }

    /**
     * 태그 AJAX요청
     */
    @PostMapping(SETTINGS_TAGS_URL + "/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null){
            tag = tagRepository.save(Tag.builder().title(title).build());
        }
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_TAGS_URL + "/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null){
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping(SETTINGS_ZONES_URL)
    public String zones_url(@CurrentUser Account account, Model model) throws JsonProcessingException {

        List<String> zones = accountService.getZones(account).stream().map(a -> a.toString()).collect(Collectors.toList());
        List<Zone> all = zoneRepository.findAll();
        List<String> collect = all.stream().map(z -> z.toString()).collect(Collectors.toList());


        model.addAttribute(account);
        model.addAttribute("zones", zones);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(collect));
        return "settings/zones";
    }
    // TODO Zone 추가, 삭제
    @PostMapping(SETTINGS_ZONES_URL + "/add")
    @ResponseBody
    public ResponseEntity zones_add(@CurrentUser Account account, @RequestBody ZoneDto zoneDto){
        return accountService.addZone(account, zoneDto);
    }
    @PostMapping(SETTINGS_ZONES_URL + "/remove")
    @ResponseBody
    public ResponseEntity zones_remove(@CurrentUser Account account, @RequestBody ZoneDto zoneDto){
        Zone zone = zoneRepository.findByLocalNameOfCity(zoneDto.getLocalNameOfCity());
        Set<Zone> zones = accountService.getZones(account);
        if(!zones.contains(zone)){
            return ResponseEntity.badRequest().build();
        }
        return accountService.removeZone(account, zone);
    }
}
