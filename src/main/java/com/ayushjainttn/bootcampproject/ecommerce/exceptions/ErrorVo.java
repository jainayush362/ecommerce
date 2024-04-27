package com.ayushjainttn.bootcampproject.ecommerce.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorVo {
    private LocalDateTime timeStamp = LocalDateTime.now();
    private String message;
    private Object description;
    private HttpStatus status;
    public ErrorVo(String message, Object description, HttpStatus status) {
        this.message = message;
        this.description = description;
        this.status = status;
    }
}
