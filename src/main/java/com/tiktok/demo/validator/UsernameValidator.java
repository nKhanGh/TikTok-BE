package com.tiktok.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<UsernameConstraint, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if(username.isEmpty()) {
            buildConstraint(context, "USERNAME_EMPTY");
            return false;
        }
        if(username.length() < 8 || username.length() > 20) {
            buildConstraint(context, "USERNAME_LENGTH");
            return false;
        }

        if(!Character.isLetter(username.charAt(0))){
            buildConstraint(context, "USERNAME_FIRST_CHAR");
            return false;
        } 
            
        boolean hasLetter = username.chars().anyMatch(Character::isLetter);
        boolean hasDigit = username.chars().anyMatch(Character::isDigit);
        if(!hasLetter || !hasDigit){
            buildConstraint(context, "USERNAME_DIGIT_LETTER");
            return false;
        }
        return true;
    }

    private void buildConstraint(ConstraintValidatorContext context, String message){
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
    
}
