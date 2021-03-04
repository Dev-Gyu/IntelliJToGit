package com.studyolle.studyolle.study;

import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.study.form.BannerForm;
import com.studyolle.studyolle.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    @Transactional
    public void modifyDescription(Account account, Study byPath,StudyDescriptionForm studyDescriptionForm) {
        authCheck(account, byPath);
        modelMapper.map(studyDescriptionForm, byPath);
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

    private void authCheck(Account account, Study study){
        if(study == null){
            throw new IllegalArgumentException("존재 하지 않는 스터디입니다.");
        }
        if(!study.getManagers().contains(account)){
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }
}
