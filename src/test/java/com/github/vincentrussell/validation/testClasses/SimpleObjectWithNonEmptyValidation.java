package com.github.vincentrussell.validation.testClasses;


import com.github.vincentrussell.validation.annotation.Validation;

public class SimpleObjectWithNonEmptyValidation {

    @Validation(validators = "notEmpty", errorMessage = "you must provide a nonEmptyString")
    private String field1;

    private String unwatchedString1;
    private String unwatchedString2;

    public SimpleObjectWithNonEmptyValidation setField1(String field1) {
        this.field1 = field1;
        return this;
    }
}
