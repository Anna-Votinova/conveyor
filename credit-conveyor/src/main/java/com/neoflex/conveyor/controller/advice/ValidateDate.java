package com.neoflex.conveyor.controller.advice;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateDate.DateValidator.class)
public @interface ValidateDate {

    String message() default "{message.key}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    class DateValidator implements ConstraintValidator<ValidateDate, LocalDate> {

        private final LocalDate startDate = LocalDate.now().minusYears(18L);


        @Override
        public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
            return date.isBefore(startDate);
        }
    }

}
