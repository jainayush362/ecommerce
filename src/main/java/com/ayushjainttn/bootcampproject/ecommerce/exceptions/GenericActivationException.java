package com.ayushjainttn.bootcampproject.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GenericActivationException extends RuntimeException{
    public GenericActivationException(String s){
        super(s);
    }
}
