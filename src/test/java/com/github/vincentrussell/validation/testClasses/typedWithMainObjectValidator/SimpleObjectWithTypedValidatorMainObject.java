package com.github.vincentrussell.validation.testClasses.typedWithMainObjectValidator;


import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.ValidatorWithMainObject;
import com.github.vincentrussell.validation.annotation.Validation;

public class SimpleObjectWithTypedValidatorMainObject {

    @Validation(validators = "stringNotEqualToBOOM", errorMessage = "you must provide a value for string field1")
    private String field1;

    @Validation(validators = "integerNotNull", errorMessage = "you must provide a value for integer field2")
    private Integer field2;

    private String unwatchedString1;
    private String unwatchedString2;

    public SimpleObjectWithTypedValidatorMainObject setField1(String field1) {
        this.field1 = field1;
        return this;
    }

    public Integer getField2() {
        return field2;
    }

    public SimpleObjectWithTypedValidatorMainObject setField2(Integer field2) {
        this.field2 = field2;
        return this;
    }

    public static class StringNotNullMainObject implements ValidatorWithMainObject<String, SimpleObjectWithTypedValidatorMainObject> {


        public static final String BOOM = "BOOM";
        private SimpleObjectWithTypedValidatorMainObject mainObject = null;

        public SimpleObjectWithTypedValidatorMainObject getMainObject() {
            return mainObject;
        }

        @Override
        public String getName() {
            return "stringNotEqualToBOOM";
        }

        @Override
        public ValidationError validate(String object, SimpleObjectWithTypedValidatorMainObject mainObject) {
            this.mainObject = mainObject;
            if (BOOM.equals(object)) {
                throw new IllegalArgumentException("string is equal to BOOM");
            }
            return  null;
        }
    }

}
