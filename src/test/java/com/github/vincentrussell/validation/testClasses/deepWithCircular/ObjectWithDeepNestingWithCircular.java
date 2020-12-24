package com.github.vincentrussell.validation.testClasses.deepWithCircular;

import com.github.vincentrussell.validation.annotation.Validation;

import java.util.List;

public class ObjectWithDeepNestingWithCircular {

    private List<SecondClass> secondClassList;

    public List<SecondClass> getSecondClassList() {
        return secondClassList;
    }

    public ObjectWithDeepNestingWithCircular setSecondClassList(List<SecondClass> secondClassList) {
        this.secondClassList = secondClassList;
        return this;
    }

    public static class SecondClass  {

        private List<ThirdClass> thirdClassList;

        public List<ThirdClass> getThirdClassList() {
            return thirdClassList;
        }

        public SecondClass setThirdClassList(List<ThirdClass> thirdClassList) {
            this.thirdClassList = thirdClassList;
            return this;
        }
    }

    public static class ThirdClass  {

        private List<ForthClass> forthClassList;

        public List<ForthClass> getForthClassList() {
            return forthClassList;
        }

        public ThirdClass setForthClassList(List<ForthClass> forthClassList) {
            this.forthClassList = forthClassList;
            return this;
        }
    }

    public static class ForthClass  {

        @Validation(validators = "notNull")
        private String field1;

        @Validation(validators = "notNull")
        private ObjectWithDeepNestingWithCircular circular;

        public String getField1() {
            return field1;
        }

        public ForthClass setField1(String field1) {
            this.field1 = field1;
            return this;
        }

        public ForthClass setCircular(ObjectWithDeepNestingWithCircular circular) {
            this.circular = circular;
            return this;
        }

        public ObjectWithDeepNestingWithCircular getCircular() {
            return circular;
        }
    }
}
