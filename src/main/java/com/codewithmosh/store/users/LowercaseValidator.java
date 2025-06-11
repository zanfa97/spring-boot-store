package com.codewithmosh.store.users;

import jakarta.validation.ConstraintValidator;

public class LowercaseValidator implements ConstraintValidator<Lowercase, String> {

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are considered valid
        }
        return value.equals(value.toLowerCase());
    }   

}
