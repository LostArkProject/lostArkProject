package com.teamProject.lostArkProject.common.config;

import com.teamProject.lostArkProject.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 예외를 처리하는 전역 예외 처리 클래스 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** IllegalArgumentException 예외 처리 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgument 예외가 발생하였습니다: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    
    /** IllegalArgumentException 예외 처리 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalState 예외가 발생하였습니다: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /** 커스텀 예외 처리 */
    @ExceptionHandler(UnauthorizedException.class)
    public Object handleUnauthorizedException(UnauthorizedException e) {
        log.warn("사용자가 인증되지 않았습니다: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /** 일반 예외 처리 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        log.warn("일반 예외가 발생하였습니다: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
