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
@Constraint(validatedBy = DateIsBeforeYears.DateValidator.class)
public @interface DateIsBeforeYears {

    String message() default "{validation.validateDate}";

    int years();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class DateValidator implements ConstraintValidator<DateIsBeforeYears, LocalDate> {

        private int years;

        @Override
        public void initialize(DateIsBeforeYears constraintAnnotation) {
            this.years = constraintAnnotation.years();
        }

        @Override
        public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {

            LocalDate startDate = LocalDate.now().minusYears(years);

            return date.isBefore(startDate);
        }
    }

}
