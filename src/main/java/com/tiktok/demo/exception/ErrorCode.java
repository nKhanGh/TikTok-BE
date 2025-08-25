package com.tiktok.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(9998, "Invalid message key!", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User Existed", HttpStatus.BAD_REQUEST),
    USER_FINDED(1002, "User not found", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters!", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters!", HttpStatus.BAD_REQUEST),
    DOB_INVALID(1005, "Date of birth must not be null!", HttpStatus.BAD_REQUEST),
    FIRSTNAME_INVALID(1006, "First name must not be empty!", HttpStatus.BAD_REQUEST),
    LASTNAME_INVALID(1007, "Last name must not be empty!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1008, "User does not exist!", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1009, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1010, "You do not have permission!", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(1011, "Role not found", HttpStatus.NOT_FOUND),
    INVALID_DOB(1012, "Your age must be at least {min}!", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1013, "Role does not exist!", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_EXISTED(1014, "Permission does not exist!", HttpStatus.NOT_FOUND),
    NAME_EXISTED(1015, "Name has existed!", HttpStatus.BAD_REQUEST),
    MUSIC_NOT_EXISTED(1016, "Music does not exist!", HttpStatus.NOT_FOUND),
    HASHTAG_NOT_EXISTED(1017, "Hash Tag does not exist!", HttpStatus.NOT_FOUND),
    VIDEO_NOT_EXISTED(1018, "Video does not exist!", HttpStatus.NOT_FOUND),
    COMMENT_NOT_EXISTED(1019, "Comment does not exist!", HttpStatus.NOT_FOUND),
    COMMENT_CONFLICT(1020, "Parent comment does not in child's video!", HttpStatus.BAD_REQUEST),
    INVALID_FOLLOW_STATUS(1021, "Follow status must be one of: FOLLOW, BLOCK, PENDING!", HttpStatus.NOT_FOUND),
    USER_RELATION(1022, "User relation does not exist!", HttpStatus.NOT_FOUND),
    EMAIL_INVALID(1023, "Email cannot be empty!", HttpStatus.BAD_REQUEST),
    EMAIL_SUBJECT_INVALID(1024, "Email'subject cannot be empty!", HttpStatus.BAD_REQUEST),
    EMAIL_BODY_INVALID(1025, "Body cannot be empty!", HttpStatus.BAD_REQUEST),
    VERIFY_CODE_INVALID(1026, "Verify code must have {min} character!", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1027, "This email has already existed!", HttpStatus.BAD_REQUEST),
    TOO_MANY_ATTEMPS(1028, "There are too many attemps on this verification code!", HttpStatus.BAD_REQUEST),
    VERIFY_CODE_NOT_TRUE(1029, "Verification code is expired or incorrect. Try again.", HttpStatus.BAD_REQUEST),
    USERNAME_EMPTY(1030, "Username cannot be empty.", HttpStatus.BAD_REQUEST),
    USERNAME_LENGTH(1031, "Username's length must be from 4 to 24 character.", HttpStatus.BAD_REQUEST),
    USERNAME_FIRST_CHAR(1032, "Username's first character must be letter.", HttpStatus.BAD_REQUEST),
    USERNAME_CHARACTER(1033, "Username's character must be letter, digit, (_) and (.).", HttpStatus.BAD_REQUEST),
    USERNAME_DIGIT_LETTER(1034, "Username must have both digit and character", HttpStatus.BAD_REQUEST),
    VIDEO_SIGNED_URL(1035, "Video signed url does not exist", HttpStatus.BAD_REQUEST),
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
