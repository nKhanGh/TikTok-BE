package com.tiktok.demo.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tiktok.demo.dto.ApiResponse;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;

@ControllerAdvice
public class GlobalExceptionHandler {
    // @ExceptionHandler(value = RuntimeException.class)
    // ResponseEntity<ApiResponse> handlingRunTimeException(RuntimeException exception){
    //     return ResponseEntity.badRequest().body(
    //         ApiResponse.builder()
    //             .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
    //             .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
    //         .build()
    //     );
    // }

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
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        Map<String, Object> attributes = null;
        
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            var constraintViolation = exception
                .getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);

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

    private String mapAttribute(String message, Map<String, Object> attributes){
        String minValue = attributes.get("min").toString();

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
