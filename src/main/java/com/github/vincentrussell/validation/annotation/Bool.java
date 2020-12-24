package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.defaultValidators.BoolValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * If the field is a boolean, assures that value matches to provided value; otherwise returns null.
 */
@Target(value = {FIELD})
@Retention(value = RUNTIME)
@Documented
@Validator(name = "com.github.vincentrussell.validation.annotation.Bool",
        validatedBy = BoolValidator.class)
public @interface Bool {

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
     * The annotated element must be a boolean that matches the specified value.
     *
     * @return nothing.
     */
    boolean value() default true;

}
