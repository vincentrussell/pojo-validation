package com.github.vincentrussell.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation from constructor parameters so that we can track the name of the parameter because
 * getting parameter names is possible only if debug information is included during compilation.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConstructorParam {

    /**
     * The name of the parameter for the constructor.
     *
     * @return do nothing.
     */
    String value() default "";

}
