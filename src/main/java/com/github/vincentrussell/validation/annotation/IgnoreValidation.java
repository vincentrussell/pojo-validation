package com.github.vincentrussell.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can used to skip validation for a particular class.
 */
@Documented
@Target(value = TYPE)
@Retention(value = RUNTIME)
public @interface IgnoreValidation {

}
