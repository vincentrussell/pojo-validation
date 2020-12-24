package com.github.vincentrussell.validation.testClasses.nestedMaps;

import com.github.vincentrussell.validation.testClasses.simple.SimpleObject;

import java.util.Map;

public class ObjectWithNestedMapObjects {
    private Map<String, SimpleObject> simpleObjectsMap;

    public ObjectWithNestedMapObjects setSimpleObjectsMap(Map<String, SimpleObject> simpleObjectsMap) {
        this.simpleObjectsMap = simpleObjectsMap;
        return this;
    }

    public Map<String, SimpleObject> getSimpleObjectsMap() {
        return simpleObjectsMap;
    }
}
