package com.ayushjainttn.bootcampproject.ecommerce.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorVo> handleAllException(Exception ex, WebRequest request){
        ErrorVo errorFormat = new ErrorVo(ex.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorFormat, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public final ResponseEntity<ErrorVo> handleUserAlreadyExistsException(Exception ex, WebRequest request) throws UserAlreadyExistsException{
        ErrorVo errorFormat = new ErrorVo(ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GenericActivationException.class)
    public final ResponseEntity<ErrorVo> handleGenericActivationException(Exception ex, WebRequest request) throws GenericActivationException {
        ErrorVo errorFormat = new ErrorVo(ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorVo> handleResourceNotFoundException(Exception ex, WebRequest request) throws ResourceNotFoundException {
        ErrorVo errorFormat = new ErrorVo(ex.getMessage(), request.getDescription(false), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorFormat, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> validationErrors = new ArrayList<>();
        Map<String,String> validationErrorMap = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(error.getField() + ": " + error.getDefaultMessage());
            validationErrorMap.put(error.getField(),error.getDefaultMessage());
        }
        ErrorVo errorFormat = new ErrorVo("Validation error, check details for info!", validationErrorMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }
}
