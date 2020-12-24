package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.defaultValidators.RegexValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that the value a string and match the specified Regex.
 */
@Target(value = {FIELD})
@Retention(value = RUNTIME)
@Documented
@Validator(name = "com.github.vincentrussell.validation.annotation.Regex",
        validatedBy = RegexValidator.class)
public @interface Regex {

    /**
     * the message that will be used when validation fails.
     *
     * @return nothing.
     */
    String errorMessage() default "";

    /**
     * the types that this validation applies to. A {@link com.github.vincentrussell.validation.type.TypeDeterminer}
     * is required.
     *
     * @return nothing.
     */
    String[] types() default {};

    /**
     * The regex to match.
     *
     * @return nothing.
     */
    String regex();

    /**
     * The flags to use with the regex.  Defaults to 0.
     * @return nothing.
     */
    int flags() default 0;

}
