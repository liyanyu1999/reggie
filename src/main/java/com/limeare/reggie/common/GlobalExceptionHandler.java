package com.limeare.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;


@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
//全局异常处理
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split [2]+"已存在";
            return R.error(msg);
        }
        return R.error("系统忙，请稍后重试...");
    }

    @ExceptionHandler(CustomerException.class)
    public R<String> exceptionHandler(CustomerException ex) {
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
