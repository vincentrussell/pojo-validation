package com.github.vincentrussell.validation.testClasses.typedValidator;


import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.Validation;

public class SimpleObjectWithTypedValidator {

    @Validation(validators = "stringNotNull", errorMessage = "you must provide a value for string field1")
    private String field1;

    @Validation(validators = "integerNotNull", errorMessage = "you must provide a value for integer field2")
    private Integer field2;

    private String unwatchedString1;
    private String unwatchedString2;

    public SimpleObjectWithTypedValidator setField1(String field1) {
        this.field1 = field1;
        return this;
    }

    public Integer getField2() {
        return field2;
    }

    public SimpleObjectWithTypedValidator setField2(Integer field2) {
        this.field2 = field2;
        return this;
    }

    public static class StringNotNull implements Validator<String> {

        @Override
        public String getName() {
            return "stringNotNull";
        }

        @Override
        public ValidationError validate(String string) {
            if (string == null) {
                throw new NullPointerException("string");
            }
            return  null;
        }
    }

    public static class IntegerNotNull implements Validator<String> {

        @Override
        public String getName() {
            return "integerNotNull";
        }

        @Override
        public ValidationError validate(String string) {
            if (string == null) {
                throw new NullPointerException("string");
            }
            return  null;
        }
    }

}
