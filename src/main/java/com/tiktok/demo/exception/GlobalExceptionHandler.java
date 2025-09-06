package com.tiktok.demo.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

import jakarta.validation.metadata.ConstraintDescriptor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tiktok.demo.dto.ApiResponse;

import jakarta.validation.ConstraintViolation;

@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {
    @SuppressWarnings("unused")
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getStatusCode()).body(
            ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
            .build()
        );
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handlingNotValidException(MethodArgumentNotValidException exception){
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if(fieldError == null )
            throw new AppException(ErrorCode.INVALID_KEY);
        String enumKey = fieldError.getDefaultMessage() ;
        ErrorCode errorCode;
        Map<String, Object> attributes;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            var constraintViolation = fieldError.unwrap(ConstraintViolation.class);

            ConstraintDescriptor<?> descriptor = constraintViolation.getConstraintDescriptor();
            attributes = descriptor.getAttributes();
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return ResponseEntity.status(errorCode.getStatusCode()).body(
            ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(Objects.nonNull(attributes)
                    ? mapAttribute(errorCode.getMessage(), attributes)
                    : errorCode.getMessage())
            .build()
        );
    }

    private String mapAttribute(String message, Map<String, Object> attributes){
        String minValue = attributes.get("min") != null 
            ? attributes.get("min").toString()
            : "";

        return message.replace("{min}", minValue);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
            ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build()
        );
    }
}
