package com.tiktok.demo.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tiktok.demo.dto.ApiResponse;

import jakarta.validation.ConstraintViolation;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getStatusCode()).body(
            ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
            .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingNotValidException(MethodArgumentNotValidException exception){
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String enumKey = fieldError.getDefaultMessage() ;
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        Map<String, Object> attributes = null;
        
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            var constraintViolation = fieldError.unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
        } catch (IllegalArgumentException e) {
        }

        return ResponseEntity.status(errorCode.getStatusCode()).body(
            ApiResponse.builder()
                .code(errorCode.getCode())
                .message(Objects.nonNull(attributes)
                    ? mapAttribute(errorCode.getMessage(), attributes)
                    : errorCode.getMessage())
            .build()
        );
    }
        
    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex){
    //     FieldError fieldError = ex.getBindingResult().getFieldError();
    //     String enumKey = fieldError.getDefaultMessage(); // USERNAME_DIGIT_LETTER, USERNAME_EMPTY, ...
        
    //     ErrorCode errorCode;
    //     try {
    //         errorCode = ErrorCode.valueOf(enumKey);
    //     } catch (IllegalArgumentException e) {
    //         errorCode = ErrorCode.INVALID_KEY;
    //     }

    //     return ResponseEntity.status(errorCode.getStatusCode())
    //             .body(ApiResponse.builder()
    //                     .code(errorCode.getCode())
    //                     .message(errorCode.getMessage())
    //                     .build());
    // }



    private String mapAttribute(String message, Map<String, Object> attributes){
        String minValue = attributes.get("min") != null 
            ? attributes.get("min").toString()
            : "";

        return message.replace("{min}", minValue);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
            ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build()
        );
    }
}
