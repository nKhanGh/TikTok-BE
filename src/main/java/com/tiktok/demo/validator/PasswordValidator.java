package com.tiktok.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String>{

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if(password == null || password.length() < 8 || password.length() > 21){
            buildConstraint(context, "PASSWORD_LENGTH");
            return false;
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        if(!hasLetter || !hasDigit || !hasSpecial){
            buildConstraint(context, "PASSWORD_CHAR");
            return false;
        }

        return true;
    }

    private void buildConstraint(ConstraintValidatorContext context, String message){
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }


    
}
