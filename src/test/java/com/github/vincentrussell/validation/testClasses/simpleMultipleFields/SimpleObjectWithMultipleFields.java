package com.github.vincentrussell.validation.testClasses.simpleMultipleFields;


import com.github.vincentrussell.validation.annotation.Validation;

public class SimpleObjectWithMultipleFields {

    @Validation(validators = "notNull", errorMessage = "you must provide a value for field1")
    private String field1;

    @Validation(validators = "notNull", errorMessage = "you must provide a value for field2")
    private String field2;

    private String unwatchedString1;
    private String unwatchedString2;

    public SimpleObjectWithMultipleFields setField1(String field1) {
        this.field1 = field1;
        return this;
    }

    public SimpleObjectWithMultipleFields setField2(String field2) {
        this.field2 = field2;
        return this;
    }
}
