package com.github.vincentrussell.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to make an alias for link in the path chain when setting the field
 * on {@link com.github.vincentrussell.validation.ValidationError}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PathAlias {

    /**
     * with alias the field that is used when
     * {@link com.github.vincentrussell.validation.ValidationError#setField(java.util.Stack)} is called.
     * The patch stack will default to the name of the field.  This will replace that.
     *
     * @return nothing.
     */
    String value();
}
