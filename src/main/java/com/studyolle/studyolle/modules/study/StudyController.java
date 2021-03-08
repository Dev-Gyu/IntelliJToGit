package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.modules.account.CurrentUser;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.study.form.StudyForm;
import com.studyolle.studyolle.modules.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudyFormValidator studyFormValidator;
    private final StudyRepository studyRepository;

    @InitBinder("studyForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new StudyForm());
        return "study/form";
    }
    @PostMapping("/new-study")
    public String newStudyForm_do(@CurrentUser Account account, @Valid @ModelAttribute StudyForm studyForm,
                                  Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "study/form";
        }
        Study newStudy = studyService.createNewStudy(modelMapper.map(studyForm, Study.class), account);

        return "redirect:/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable String path, Model model){
        Study byPath = studyRepository.findByPath(path);
        if(byPath == null){
            throw new IllegalArgumentException("존재하지 않는 스터디 입니다.");
        }
        model.addAttribute(account);
        model.addAttribute(studyRepository.findByPath(path));
        return "/study/view";
    }
    @GetMapping("/study/{path}/members")
    public String viewMember(@CurrentUser Account account, @PathVariable String path, Model model){
        Study byPath = studyRepository.findByPath(path);
        if(byPath == null){
            throw new IllegalArgumentException("존재하지 않는 스터디 입니다.");
        }
        model.addAttribute(account);
        model.addAttribute(studyRepository.findByPath(path));
        return "/study/members";
    }

    @PostMapping("/study/{path}/join")
    public String join(@CurrentUser Account account, @PathVariable String path){
        studyService.addMember(account, path);
        return "redirect:/study/" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
    @PostMapping("/study/{path}/withdrawal")
    public String withdrawal(@CurrentUser Account account, @PathVariable String path){
        studyService.withDrawal(account, path);
        return "redirect:/study/" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
