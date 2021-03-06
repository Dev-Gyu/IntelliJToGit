package com.studyolle.studyolle.event;

import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.domain.Event;
import com.studyolle.studyolle.event.validator.EventValidator;
import com.studyolle.studyolle.study.Study;
import com.studyolle.studyolle.study.StudyRepository;
import com.studyolle.studyolle.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {
    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String new_event(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study = studyRepository.findStudyManagerByPath(path);
        studyService.authCheck(account, study);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String new_event_do(@CurrentUser Account account, @PathVariable String path,
                               @Valid EventForm eventForm, Errors errors, Model model){
        Study study = studyRepository.findTagsManagerByPath(path);
        studyService.authCheck(account, study);
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }
        Event event = eventService.createEvent(study, modelMapper.map(eventForm, Event.class), account);
        return "redirect:/study/" + URLEncoder.encode(path, UTF_8) + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String event_detail(@CurrentUser Account account, @PathVariable String path,@PathVariable Long id, Model model){
        Optional<Event> event = eventRepository.findAllEventEnrollMemberStudyById(id);
        Study study = studyRepository.findStudyManagerByPath(path);
        model.addAttribute(account);
        model.addAttribute(event.orElseThrow());
        model.addAttribute(study);
        return "event/detail";
    }

    @GetMapping("/events")
    public String events(@CurrentUser Account account, @PathVariable String path, Model model){
        List<Event> newEvents = eventService.findNewEvents(path);
        List<Event> oldEvents = eventService.findOldEvents(path);
        model.addAttribute(account);
        model.addAttribute(studyRepository.findByPath(path));
        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);
        return "event/list";
    }

    @GetMapping("/events/{id}/edit")
    public String event_update(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                               @ModelAttribute EventForm eventForm, Model model){
        Optional<Event> byId = eventRepository.findById(id);
        model.addAttribute(account);
        model.addAttribute(studyRepository.findByPath(path));
        model.addAttribute(modelMapper.map(byId.orElseThrow(), EventForm.class));
        return "event/update";
    }
    @PostMapping("/events/{id}/edit")
    public String events_update_do(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                                   @Valid @ModelAttribute EventForm eventForm, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(studyRepository.findByPath(path));
            return "event/update";
        }
        eventService.updateEvent(eventForm, errors, id);
        return "redirect:/study/" + URLEncoder.encode(path, UTF_8) + "/events/" + id + "/edit";
    }

    /**
     * 스프링부트 application.properties에 DeleteMapping 쓰기위한 설정 해두고 타임리프의 form태그 method='delete'하면 타임리프가 name:_method로 value:delete 맵핑시켜줌
     * 이걸로 @DeleteMapping 받을수 있다.
     */
    @DeleteMapping("/events/{id}")
    public String events_delete(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id){
        Study study = studyRepository.findStudyManagerByPath(path);
        studyService.authCheck(account, study);
        eventService.deleteEvent(eventRepository.findById(id).orElseThrow());
        return "redirect:/study/" + URLEncoder.encode(path, UTF_8) + "/events";
    }

    @PostMapping("events/{id}/enroll")
    public String events_enroll(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id){
        // TODO 해당 이벤트가 선착순인지, 관리자 확인방식인지에 따라 참석확정여부 변경하는 로직 따로 구현할것(현재 선착순만 구현해둠)
        // TODO 해당 계정이 스터디에 가입되어있는지 여부 확인, id에 해당하는 이벤트 있는지 여부 확인
        eventService.enroll(account, id);
        return "redirect:/study/" + URLEncoder.encode(path,UTF_8) + "/events/" + id;
    }
    @PostMapping("events/{id}/disenroll")
    public String events_disenroll(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id){
        // TODO 해당 계정이 스터디에 가입되어있는지 여부 확인, id에 해당하는 이벤트 있는지 여부 확인, 해당 계정이 모임에 참석등록 되어있는지 확인
        eventService.disEnroll(account, id);
        return "redirect:/study/" + URLEncoder.encode(path,UTF_8) + "/events/" + id;
    }

}
