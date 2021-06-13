package com.github.vincentrussell.validation.defaultValidators;

import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.ConstructorParam;
import com.github.vincentrussell.validation.util.ValidationUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link Validator} that is used to validate that the value matches the specified regex.
 */
public final class RegexValidator implements Validator {

    private final Pattern pattern;

    /**
     * Default constructor.
     *
     * @param regex the regex to use
     * @param flags the flags for the regex
     */
    public RegexValidator(@ConstructorParam("regex") final String regex, @ConstructorParam("flags") final int flags) {
        this.pattern = Pattern.compile(regex, flags);
    }

    @Override
    public ValidationError validate(final Object object) {
        if (object == null) {
            return null;
        }
        if (!CharSequence.class.isInstance(object)) {
            return ValidationUtils.isTrue(false, "value %s is not a string", object);
        }
        Matcher matcher = pattern.matcher((CharSequence) object);
        return ValidationUtils.isTrue(matcher.matches(), "string %s does not match pattern %s", object, pattern);
    }


}
