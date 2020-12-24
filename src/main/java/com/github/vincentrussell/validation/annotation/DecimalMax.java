package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.defaultValidators.DecimalMaxValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The field value must be a number whose value must be lower or equal to the specified maximum.
 *
 * Supported types are:
 * <ul>
 * <li>BigDecimal</li>
 * <li>BigInteger</li>
 * <li>byte, double, float, int, long, short and their respective wrappers or any subclass of Number</li>
 * </ul>
 */
@Target(value = {FIELD})
@Retention(value = RUNTIME)
@Documented
@Validator(name = "com.github.vincentrussell.validation.annotation.DecimalMax",
        validatedBy = DecimalMaxValidator.class)
public @interface DecimalMax {

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
     * The String representation of the max value according to the BigDecimal string representation.
     *
     * @return nothing.
     */
    String value();

}
