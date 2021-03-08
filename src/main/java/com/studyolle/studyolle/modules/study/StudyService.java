package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.study.event.StudyCreatedEvent;
import com.studyolle.studyolle.modules.study.event.StudyUpdateEvent;
import com.studyolle.studyolle.modules.study.form.BannerForm;
import com.studyolle.studyolle.modules.study.form.StudyDescriptionForm;
import com.studyolle.studyolle.modules.tag.Tag;
import com.studyolle.studyolle.modules.tag.TagRepository;
import com.studyolle.studyolle.modules.zone.Zone;
import com.studyolle.studyolle.modules.zone.ZoneDto;
import com.studyolle.studyolle.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {
    private final StudyRepository studyRepository;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    @Transactional
    public void modifyDescription(Account account, Study study,StudyDescriptionForm studyDescriptionForm) {
        authCheck(account, study);
        modelMapper.map(studyDescriptionForm, study);
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 소개를 수정했습니다."));
    }
    @Transactional
    public void changeUseBanner(Account account, String path){
        Study byPath = studyRepository.findByPath(path);
        authCheck(account, byPath);
        byPath.setUseBanner(!byPath.isUseBanner());
    }

    @Transactional
    public void changeBanner(Account account, String path, BannerForm bannerForm) {
        Study byPath = studyRepository.findByPath(path);
        authCheck(account, byPath);
        byPath.setImage(bannerForm.getImage());
    }

    @Transactional
    public void addTag(Account account, StudyTagForm studyTagForm) {
        Study byPath = studyRepository.findByPath(studyTagForm.getPath());
        authCheck(account, byPath);
        Tag newTag = tagRepository.findByTitle(studyTagForm.getTitle());
        if(newTag == null){
            newTag = modelMapper.map(studyTagForm, Tag.class);
            tagRepository.save(newTag);
        }
        byPath.getTags().add(newTag);
    }

    public void authCheck(Account account, Study study){
        if(study == null){
            throw new IllegalArgumentException("존재 하지 않는 스터디입니다.");
        }
        if(!study.getManagers().contains(account)){
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    @Transactional
    public void removeTag(Account account, StudyTagForm studyTagForm) {
        Study study = studyRepository.findTagsManagerByPath(studyTagForm.getPath());
        authCheck(account, study);
        Tag byTitle = tagRepository.findByTitle(studyTagForm.getTitle());
        study.getTags().remove(byTitle);
    }

    @Transactional
    public void addZone(Account account, String path, ZoneDto zoneDto) {
        Study study = studyRepository.findZonesManagersByPath(path);
        authCheck(account, study);
        Zone findZone = zoneRepository.findByLocalNameOfCity(zoneDto.getLocalNameOfCity());
        if(!study.getZones().contains(findZone)) {
            study.getZones().add(findZone);
        }
    }

    @Transactional
    public void removeZone(Account account, String path, ZoneDto zoneDto) {
        Study study = studyRepository.findZonesManagersByPath(path);
        authCheck(account, study);
        Zone target = zoneRepository.findByLocalNameOfCity(zoneDto.getLocalNameOfCity());
        if(study.getZones().contains(target)) {
            study.getZones().remove(target);
        }
    }
    @Transactional
    public Study changeStatus(Account account, String path) {
        Study study = studyRepository.findStudyManagerByPath(path);
        authCheck(account, study);
        checkStatus(study);
        return study;
    }
    @Transactional
    public Study changeRecruiting(Account account, String path){
        Study study = studyRepository.findStudyManagerByPath(path);
        authCheck(account, study);
        checkRecruiting(study);
        return study;
    }
    @Transactional
    public void modifyPath(Account account, String path, String newPath) {
        Study study = studyRepository.findStudyManagerByPath(path);
        authCheck(account, study);
        study.setPath(newPath);
    }
    @Transactional
    public void modifyTitle(Account account, String path, String title) {
        Study study = studyRepository.findStudyManagerByPath(path);
        authCheck(account, study);
        study.setTitle(title);
    }

    @Transactional
    public void delete(Account account, String path) {
        Study study = studyRepository.findStudyManagerByPath(path);
        authCheck(account, study);
        studyRepository.delete(study);
    }

    @Transactional
    public void addMember(Account account, String path) {
        Study study = studyRepository.findStudyMemberByPath(path);
        if(study == null){
            throw new IllegalArgumentException("존재하지 않는 스터디입니다.");
        }
        if(!study.getMembers().contains(account)){
            study.getMembers().add(account);
            study.setMemberCount(study.getMemberCount() + 1);
        }
    }
    @Transactional
    public void withDrawal(Account account, String path) {
        Study study = studyRepository.findStudyMemberByPath(path);
        if(!study.getMembers().contains(account)){
            throw new IllegalArgumentException("잘못된 요청 입니다.");
        }
        study.getMembers().remove(account);
        study.setMemberCount(study.getMemberCount() - 1);
    }

    private void checkRecruiting(Study study){
        if(study.getRecruitingUpdateDateTime() == null){
          study.setRecruitingUpdateDateTime(LocalDateTime.now());
        } else if(LocalDateTime.now().isBefore(study.getRecruitingUpdateDateTime().plusHours(1))){
            throw new RuntimeException("회원 모집 상태 변경은 1시간에 한 번 가능합니다.\n 마지막 변경시간 = " + study.getRecruitingUpdateDateTime());
        }
        if(!study.isRecruiting()){
            study.setRecruiting(true);
            study.setRecruitingUpdateDateTime(LocalDateTime.now());
            eventPublisher.publishEvent(new StudyUpdateEvent(study, "팀원 모집을 시작합니다."));
        }else if(study.isRecruiting()){
            study.setRecruiting(false);
            study.setRecruitingUpdateDateTime(LocalDateTime.now());
            eventPublisher.publishEvent(new StudyUpdateEvent(study, "팀원 모집을 중단했습니다."));
        }
    }

    private void checkStatus(Study study){
        if(study.isClosed()){
            throw new RuntimeException("이미 종료된 스터디는 다시 공개할 수 없습니다.");
        }

        if(!study.isPublished()) {
            study.setPublished(true);
            study.setPublishDateTime(LocalDateTime.now());
            eventPublisher.publishEvent(new StudyCreatedEvent(study));
        }else if(study.isPublished()){
            study.setClosed(true);
            study.setPublished(false);
            study.setClosedDateTime(LocalDateTime.now());
            study.setRecruiting(false);
            eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디를 종료했습니다."));
        }
    }
}
