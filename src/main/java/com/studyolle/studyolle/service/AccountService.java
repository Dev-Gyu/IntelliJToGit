package com.studyolle.studyolle.service;

import com.studyolle.studyolle.account.UserAccount;
import com.studyolle.studyolle.config.AppProperties;
import com.studyolle.studyolle.domain.Tag;
import com.studyolle.studyolle.domain.Zone;
import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.account.SignupForm;
import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.mail.EmailMessage;
import com.studyolle.studyolle.mail.EmailService;
import com.studyolle.studyolle.main.EmailLoginDto;
import com.studyolle.studyolle.settings.NicknameDto;
import com.studyolle.studyolle.settings.NotificationForm;
import com.studyolle.studyolle.settings.PasswordForm;
import com.studyolle.studyolle.settings.Profile;
import com.studyolle.studyolle.zone.ZoneDto;
import com.studyolle.studyolle.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Template;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ZoneRepository zoneRepository;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @Transactional
    public Account processNewAccount(SignupForm signupForm) {
        // 회원 저장
        Account newAccount = saveNewAccount(signupForm);
        // 회원 가입 후 메일인증
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(SignupForm signupForm) {
        signupForm.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        Account account = modelMapper.map(signupForm, Account.class);
        account.generateToken();

        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        // 타임리프에서 제공해주는 TemplateEngine을 이용하면 HTML코드를 쉽게 문자열로 변환 할 수 있음
        // Context() = Model이라 보면됨. 타임리프에서 사용하는 변수값 설정해주는 역할

        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올레 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("email/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디올레, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);

//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(newAccount.getEmail());
//        mailMessage.setSubject("스터디올래, 회원 가입 인증");
//        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
//                "&email=" + newAccount.getEmail());
//        javaMailSender.send(mailMessage);
    }


    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null){
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if(account == null){
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }

    @Transactional
    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    @Transactional
    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);
//        account.setUrl(profile.getUrl());
//        account.setOccupation(profile.getOccupation());
//        account.setLocation(profile.getLocation());
//        account.setBio(profile.getBio());
//        account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account);
        login(account);
    }

    @Transactional
    public void updatePassword(Account account, PasswordForm passwordForm) {
        account.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        accountRepository.save(account);
    }

    @Transactional
    public void updateNotification(Account account, NotificationForm notificationForm){
        modelMapper.map(notificationForm, account); // ModelMapper가 맵핑할 멤버이름이 복잡하면 그 이름을 구분할 수 있도록 따로 설정해줘야됨
//        account.setStudyCreatedByEmail(notificationForm.isStudyCreatedByEmail());
//        account.setStudyCreatedByWeb(notificationForm.isStudyCreatedByWeb());
//        account.setStudyEnrollmentResultByEmail(notificationForm.isStudyEnrollmentResultByEmail());
//        account.setStudyEnrollmentResultByWeb(notificationForm.isStudyEnrollmentResultByWeb());
//        account.setStudyUpdatedByWeb(notificationForm.isStudyUpdatedByWeb());
//        account.setStudyUpdatedByEmail(notificationForm.isStudyUpdatedByEmail());
        accountRepository.save(account);
    }

    @Transactional
    public void updateNickname(Account account, NicknameDto nicknameDto) {
        account.setNickname(nicknameDto.getNickname());
        accountRepository.save(account);
        login(account);
    }

    @Transactional
    public void emailTokenSend(Account account, EmailLoginDto emailLoginDto, BindingResult result) {
        if(account == null){
            result.rejectValue("email",null,"존재하지 않는 이메일 입니다.");
            return;
        }

        // 타임리프에서 제공해주는 TemplateEngine을 이용하면 HTML코드를 쉽게 문자열로 변환 할 수 있음
        // Context() = Model이라 보면됨. 타임리프에서 사용하는 변수값 설정해주는 역할

        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "스터디올레 로그인하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("email/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("스터디올레, 로그인 링크")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    @Transactional
    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    @Transactional
    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }
    @Transactional
    public ResponseEntity addZone(Account account, ZoneDto zoneDto) {
        Zone zone = zoneRepository.findByLocalNameOfCity(zoneDto.getLocalNameOfCity());
        List<Zone> all = zoneRepository.findAll();
        if(!all.contains(zone)){
            return ResponseEntity.badRequest().build();
        }
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
        return ResponseEntity.ok().build();
    }
    @Transactional
    public ResponseEntity removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
        return ResponseEntity.ok().build();
    }
}
