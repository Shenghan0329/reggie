package com.John.reggie.Common;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error("SQL QUERY FAIL");
        String error = ex.getMessage();
        if(error.contains("Duplicate entry")){
            String account = error.split(" ")[2];
            return R.error("SQL QUERY FAIL: Duplicate User Account: "+account);
        }
        return R.error("SQL QUERY FAIL: Unknown Error");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        String error = ex.getMessage();
        return R.error(error);
    }
}
