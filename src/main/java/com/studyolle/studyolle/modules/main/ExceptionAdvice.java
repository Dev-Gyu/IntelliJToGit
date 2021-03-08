package com.studyolle.studyolle.modules.main;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public String handleRuntimeException(@CurrentUser Account account, HttpServletRequest request, HttpServletResponse response, RuntimeException e){
        if(account != null){
            log.info("'{}' requested '{}'", account.getNickname(), request.getRequestURI());
        } else{
            log.info("requested '{}'", request.getRequestURI());
        }
        log.error("bad request", e);
        return "error";
    }
}
