package com.striveh.pushdata.config;

import com.striveh.pushdata.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Result handleValidException(MethodArgumentNotValidException e) {
        return Result.error(e.getMessage(),Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpClientErrorException.class})
    public Result httpClientErrorException(HttpClientErrorException e) {
        log.error("HttpClientErrorException",e);
        return Result.error(e.getMessage(),"调用服务异常");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public Result handleException(Exception e) {
        log.error("系统异常",e);
        return Result.error(e.getMessage(),"系统异常");
    }
}