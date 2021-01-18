package com.demo.contentcenter.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorBody> error(SecurityException e) {
        log.warn("发生SecurityException异常", e);
        ResponseEntity<ErrorBody> responseEntity = new ResponseEntity<>(
                ErrorBody.builder()
                        .body("token不合法！")
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build(),
                HttpStatus.UNAUTHORIZED);
        return responseEntity;
    }

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ErrorBody {
    private String body;
    private Integer status;
}
