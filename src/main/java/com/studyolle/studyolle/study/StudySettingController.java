package com.studyolle.studyolle.study;

import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.study.form.BannerForm;
import com.studyolle.studyolle.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingController {
    private final StudyRepository studyRepository;
    private final StudyService studyService;

    @GetMapping("/description")
    public String description(@CurrentUser Account account, @PathVariable("path") String path, StudyDescriptionForm studyDescriptionForm, Model model){
        Study byPath = studyRepository.findByPath(path);
        studyDescriptionForm.setShortDescription(byPath.getShortDescription());
        studyDescriptionForm.setFullDescription(byPath.getFullDescription());

        model.addAttribute(account);
        model.addAttribute(byPath);
        model.addAttribute(studyDescriptionForm);
        return "study/settings/description";
    }
    @PostMapping("/description")
    public String description_do(@CurrentUser Account account, @PathVariable("path") String path,
                                 @Valid StudyDescriptionForm studyDescriptionForm,
                                 BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute(account);
            return "study/settings/description";
        }
        Study byPath = studyRepository.findByPath(path);
        studyService.modifyDescription(account, byPath,studyDescriptionForm);
        model.addAttribute(account);
        model.addAttribute(byPath);
        String encode = URLEncoder.encode(byPath.getPath(), StandardCharsets.UTF_8);
        return "redirect:/study/" + encode + "/settings/description";
    }

    @GetMapping("/banner")
    public String banner(@CurrentUser Account account, @PathVariable("path") String path,
                         @ModelAttribute BannerForm bannerForm, Model model){
        Study byPath = studyRepository.findByPath(path);
        model.addAttribute(account);
        model.addAttribute(byPath);
        return "study/settings/banner";
    }
    @PostMapping("/banner")
    public String banner_do(@CurrentUser Account account, @PathVariable("path") String path, @ModelAttribute BannerForm bannerForm){
        studyService.changeBanner(account, path, bannerForm);
        String encode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        return "redirect:/study/" + encode + "/settings/banner";
    }
    @PostMapping("/useBanner")
    public String useBanner_do(@CurrentUser Account account, @PathVariable("path") String path,@ModelAttribute BannerForm bannerForm){
        studyService.changeUseBanner(account, path);
        String encode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        return "redirect:/study/" + encode + "/settings/banner";
    }
}
