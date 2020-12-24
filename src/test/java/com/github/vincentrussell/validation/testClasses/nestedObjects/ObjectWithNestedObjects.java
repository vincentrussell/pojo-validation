package com.github.vincentrussell.validation.testClasses.nestedObjects;

import com.github.vincentrussell.validation.testClasses.simple.SimpleObject;

public class ObjectWithNestedObjects {
    private SimpleObject simpleObject1;

    public ObjectWithNestedObjects setSimpleObject1(SimpleObject simpleObject1) {
        this.simpleObject1 = simpleObject1;
        return this;
    }

    public SimpleObject getSimpleObject1() {
        return simpleObject1;
    }
}
