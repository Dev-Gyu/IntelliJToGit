package com.studyolle.studyolle.modules.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.modules.account.CurrentUser;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.tag.Tag;
import com.studyolle.studyolle.modules.zone.Zone;
import com.studyolle.studyolle.modules.study.form.BannerForm;
import com.studyolle.studyolle.modules.study.form.StudyDescriptionForm;
import com.studyolle.studyolle.modules.study.validator.StudySettingsValidator;
import com.studyolle.studyolle.modules.tag.TagRepository;
import com.studyolle.studyolle.modules.zone.ZoneDto;
import com.studyolle.studyolle.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingController {
    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;

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

    @GetMapping("/tags")
    public String tags(@CurrentUser Account account, @PathVariable("path") String path, Model model) throws JsonProcessingException {
        Study byPath = studyRepository.findByPath(path);
        List<String> collect = byPath.getTags().stream().map(t -> t.getTitle()).collect(Collectors.toList());

        List<Tag> all = tagRepository.findAll();
        List<String> allTags = all.stream().map(t -> t.getTitle()).collect(Collectors.toList());

        model.addAttribute(byPath);
        model.addAttribute("tags", collect);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        return "study/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity tags_add(@CurrentUser Account account, @RequestBody StudyTagForm studyTagForm, Model model){
        studyService.addTag(account, studyTagForm);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity tags_remove(@CurrentUser Account account, @RequestBody StudyTagForm studyTagForm, Model model){
        studyService.removeTag(account, studyTagForm);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String zones(@CurrentUser Account account, @PathVariable("path") String path, Model model) throws JsonProcessingException {
        List<Zone> allZone = zoneRepository.findAll();
        List<String> allZoneList = allZone.stream().map(z -> z.toString()).collect(Collectors.toList());

        Study findStudy = studyRepository.findByPath(path);
        Set<Zone> zones = findStudy.getZones();
        if(zones.isEmpty()){
            model.addAttribute("zones", Collections.EMPTY_LIST);
        }else{
            List<String> studyZonesList = zones.stream().map(z -> z.toString()).collect(Collectors.toList());
            model.addAttribute("zones", studyZonesList);
        }

        model.addAttribute(account);
        model.addAttribute(findStudy);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZoneList));
        return "study/settings/zones";
    }
    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity zones_add(@CurrentUser Account account, @RequestBody ZoneDto zoneDto,
                            Model model){
        studyService.addZone(account, zoneDto.getPath(), zoneDto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity zones_remove(@CurrentUser Account account, @RequestBody ZoneDto zoneDto,
                                    Model model){
        studyService.removeZone(account, zoneDto.getPath(), zoneDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String study(@CurrentUser Account account, @PathVariable("path") String path,
                        Model model){
        model.addAttribute(studyRepository.findByPath(path));
        model.addAttribute(account);
        return "study/settings/study";
    }

    @PostMapping ("/study/status")
    public String study_status(@CurrentUser Account account, @PathVariable("path") String path, Model model){
        studyService.changeStatus(account, path);
        model.addAttribute(account);
        String encode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        return "redirect:/study/" + encode + "/settings/study";
    }

    @PostMapping ("/study/recruiting")
    public String study_recruiting(@CurrentUser Account account, @PathVariable("path") String path, Model model){
        studyService.changeRecruiting(account, path);
        model.addAttribute(account);
        String encode = URLEncoder.encode(path, StandardCharsets.UTF_8);
        return "redirect:/study/" + encode + "/settings/study";
    }

    @PostMapping("/study/modifyPath")
    public String modifyPath(@CurrentUser Account account, @PathVariable("path") String path,
                             @RequestParam("newPath") String newPath, Model model){
        StudySettingsValidator studySettingsValidator = new StudySettingsValidator(studyRepository);
        if(!studySettingsValidator.isPathOkay(newPath)){
            model.addAttribute("pathError","중복되거나 올바르지 않은 경로 입니다.");
            return "study/settings/study";
        }

        studyService.modifyPath(account, path, newPath);
        return "redirect:/study/" + URLEncoder.encode(newPath, StandardCharsets.UTF_8) + "/settings/study";

    }
    @PostMapping("/study/modifyTitle")
    public String modifyTitle(@CurrentUser Account account, @PathVariable("path") String path,
                             @RequestParam("title") String title, Model model){
        StudySettingsValidator studySettingsValidator = new StudySettingsValidator(studyRepository);
        if(!studySettingsValidator.isTitleOkay(title)){
            model.addAttribute("titleError","올바르지 않은 스터디 이름 입니다.");
            return "study/settings/study";
        }
        studyService.modifyTitle(account, path, title);
        return "redirect:/study/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/settings/study";
    }
    @PostMapping("/study/delete")
    public String delete(@CurrentUser Account account, @PathVariable("path") String path){
        studyService.delete(account, path);
        return "redirect:/";
    }
}
