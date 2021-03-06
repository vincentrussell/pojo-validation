package com.github.vincentrussell.validation.testClasses.withType;

import com.github.vincentrussell.validation.annotation.Validation;

public class ObjectWithType {


    private SubType subType;
    private String type;

    public String getType() {
        return type;
    }

    public ObjectWithType setType(String type) {
        this.type = type;
        return this;
    }


    public SubType getSubType() {
        return subType;
    }

    public ObjectWithType setSubType(
            SubType subType) {
        this.subType = subType;
        return this;
    }

    public static class SubType {

        @Validation(validators = "notNull", types = "typeToValidate", errorMessage = "subType.field1 is null which is incorrect")
        private String field1;

        public String getField1() {
            return field1;
        }

        public SubType setField1(String field1) {
            this.field1 = field1;
            return this;
        }
    }

}
