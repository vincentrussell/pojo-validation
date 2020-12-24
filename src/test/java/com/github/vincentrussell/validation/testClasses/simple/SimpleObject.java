package com.github.vincentrussell.validation.testClasses.simple;


import com.github.vincentrussell.validation.annotation.Validation;

public class SimpleObject {

    @Validation(validators = "notNull", errorMessage = "you must provide a value for field1")
    private String field1;

    private String unwatchedString1;
    private String unwatchedString2;

    public SimpleObject setField1(String field1) {
        this.field1 = field1;
        return this;
    }
}
