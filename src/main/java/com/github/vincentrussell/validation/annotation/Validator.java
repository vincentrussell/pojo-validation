package com.github.vincentrussell.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be put on fields to denote validation.
 */
@Documented
@Target(value = ANNOTATION_TYPE)
@Retention(value = RUNTIME)
public @interface Validator {

    /**
     * Name of this validator.  Must be provided.
     *
     * @return nothing.
     */
    String name() default "";

    /**
     * The class that will be validating.  Must be provided.
     *
     * @return nothing.
     */
    java.lang.Class<? extends com.github.vincentrussell.validation.Validator> validatedBy();

}
