package com.jafarshodiq.library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class Isbn13Validator implements ConstraintValidator<ValidIsbn13, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !value.matches("(?:97[89])\\d{10}")) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = value.charAt(i) - '0';
            sum += digit * (i % 2 == 0 ? 1 : 3);
        }
        int checkDigit = value.charAt(12) - '0';
        int calculated = (10 - (sum % 10)) % 10;
        return calculated == checkDigit;
    }
}
