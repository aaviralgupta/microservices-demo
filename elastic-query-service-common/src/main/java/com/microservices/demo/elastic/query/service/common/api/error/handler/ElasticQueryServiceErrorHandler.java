package com.microservices.demo.elastic.query.service.common.api.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ElasticQueryServiceErrorHandler {

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<String> handle(AccessDeniedException accessDeniedException) {
        log.error("Access denied exception!",accessDeniedException);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access the resource!");
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> handle(IllegalArgumentException e){
        log.error("Illegal Argument Exception!",e);
        return ResponseEntity.badRequest().body("Illegal Argument Exception : " + e.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException e){
        log.error("Runtime Exception!",e);
        return ResponseEntity.badRequest().body("Service Runtime Exception : " + e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handle(Exception e){
        log.error("Interal server error!",e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A server error occurred!");
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException e){
        log.error("Method Argument validation Exception!", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error ->
                errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
