package com.neighborhoodservice.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    /**
     * The pattern for the Ukrainian phone numbers.
     */
    private static final String PHONE_NUMBER_PATTERN = "^\\+?3?8?(0(67|68|96|97|98|93|66|95|99|[5-9][0-9])\\d{7})$";

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null) {
            return false;
        }
        return phoneNumber.matches(PHONE_NUMBER_PATTERN);
    }
}
