package com.github.vincentrussell.validation;

import java.util.Stack;

/**
 * Class that stores validation errors that are found during validation in
 * a {@link com.github.vincentrussell.validation.Validator}.
 */
public class ValidationError {
    private Throwable throwable = null;
    private String field = null;
    private String errorMessage;
    private Object value;

    /**
     * Constructor that takes a {@link java.lang.Throwable}.
     *
     * @param throwable the throwable is thrown in the {@link com.github.vincentrussell.validation.Validator}.
     */
    public ValidationError(final Throwable throwable) {
        throwable(throwable);
    }

    /**
     * constructor with options.
     *
     * @param pathStack the path for this field
     * @param value     the value that failed
     * @param e         the throwable that was thrown if any.
     */
    public ValidationError(final Stack<String> pathStack, final Object value, final Throwable e) {
        setField(pathStack);
        setValue(value);
        throwable(e);
    }

    /**
     * set the throwable.
     *
     * @param throwable the throwable.
     * @return {@link com.github.vincentrussell.validation.ValidationError} this is a builder.
     */
    public ValidationError throwable(final Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    /**
     * Get the value that was used.
     *
     * @return the value that was used.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set the value that was used.
     *
     * @param value the value that was used
     * @return {@link com.github.vincentrussell.validation.ValidationError} this is a builder.
     */
    public ValidationError setValue(final Object value) {
        this.value = value;
        return this;
    }

    /**
     * Get the associated throwable.
     *
     * @return {@link java.lang.Throwable}
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * get the field associated with this {@link com.github.vincentrussell.validation.ValidationError}.
     *
     * @return the field.
     */
    public String getField() {
        return field;
    }

    /**
     * set the field.
     *
     * @param field the text path of the field.
     * @return {@link com.github.vincentrussell.validation.ValidationError} this is a builder.
     */
    public ValidationError setField(final String field) {
        this.field = field;
        return this;
    }

    /**
     * Set the field name of this {@link com.github.vincentrussell.validation.ValidationError} to be based on the
     * {@link java.util.Stack} of field names.
     *
     * @param pathStack the path stack of field names.
     * @return {@link com.github.vincentrussell.validation.ValidationError} this is a builder.
     */
    public ValidationError setField(final Stack<String> pathStack) {
        StringBuilder stringBuilder = new StringBuilder();
        pathStack.stream().reduce(stringBuilder, (stringBuilder12, s) -> {
            if (stringBuilder12.length() > 0) {
                stringBuilder12.append(".");
            }
            return stringBuilder12.append(s);
        }, (stringBuilder1, stringBuilder2) -> stringBuilder1.append(stringBuilder2.toString()));
        return setField(stringBuilder.toString());
    }

    /**
     * get the error message.
     *
     * @return the error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the error message for this validation error.
     *
     * @param errorMessage the errorMessage
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
