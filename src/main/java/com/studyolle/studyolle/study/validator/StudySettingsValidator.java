package com.studyolle.studyolle.study.validator;

import com.studyolle.studyolle.study.StudyRepository;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudySettingsValidator {
    private String pathRegExp = "^[가-힣a-zA-Z0-9_-]{2,20}$";
    private String titleRegExp = "^[가-힣a-zA-Z0-9_-]{1,50}$";

    private Pattern pathPattern;
    private Pattern titlePattern;

    private StudyRepository studyRepository;

    public StudySettingsValidator(StudyRepository studyRepository) {
        pathPattern = Pattern.compile(pathRegExp);
        titlePattern = Pattern.compile(titleRegExp);
        this.studyRepository = studyRepository;
    }

    public boolean isPathOkay(String path) {
        if (!pathPattern.matcher(path).matches()) {
            return false;
        } else if (studyRepository.existsByPath(path)) {
            return false;
        }
        return true;
    }

    public boolean isTitleOkay(String title) {
        if (!titlePattern.matcher(title).matches()) {
            return false;
        }
        return true;
    }
}
