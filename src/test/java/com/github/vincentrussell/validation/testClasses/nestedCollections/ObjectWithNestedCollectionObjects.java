package com.github.vincentrussell.validation.testClasses.nestedCollections;

import com.github.vincentrussell.validation.annotation.PathAlias;
import com.github.vincentrussell.validation.testClasses.simple.SimpleObject;

import java.util.List;

public class ObjectWithNestedCollectionObjects {

    @PathAlias("AliasedSimpleObjects")
    private List<SimpleObject> simpleObjects;

    public ObjectWithNestedCollectionObjects setSimpleObjects(List<SimpleObject> simpleObjects) {
        this.simpleObjects = simpleObjects;
        return this;
    }

    public List<SimpleObject> getSimpleObjects() {
        return simpleObjects;
    }
}
