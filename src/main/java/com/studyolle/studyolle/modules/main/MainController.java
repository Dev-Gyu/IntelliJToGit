package com.studyolle.studyolle.modules.main;

import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.CurrentUser;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.modules.event.Enrollment;
import com.studyolle.studyolle.modules.event.EnrollmentRepository;
import com.studyolle.studyolle.modules.event.Event;
import com.studyolle.studyolle.modules.study.Study;
import com.studyolle.studyolle.modules.study.StudyRepository;
import com.studyolle.studyolle.modules.study.StudyService;
import com.studyolle.studyolle.modules.tag.Tag;
import com.studyolle.studyolle.modules.zone.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(account != null){
            Account findAccount = accountRepository.findTagsZonesByEmail(account.getEmail());
            List<Enrollment> enrolls = enrollmentRepository.findEnrollStudyByAccountAndAccepted(findAccount, true);
            List<Study> studyList = studyRepository.findFirst6StudyByAccount(account);
            model.addAttribute("account", account);
            model.addAttribute("tags", findAccount.getTags());
            model.addAttribute("zones", findAccount.getZones());
            model.addAttribute("enrolls", enrolls);
            model.addAttribute("studyList", studyList);
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

    @GetMapping("/search/study")
    public String searchStudy(@PageableDefault(size = 9, sort = "publishDateTime",
                                direction = Sort.Direction.DESC) Pageable pageable, String keyword, Model model){
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishDateTime") ? "publishDateTime": "memberCount");
        return "search";
    }
}
