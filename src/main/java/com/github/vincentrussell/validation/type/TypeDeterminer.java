package com.github.vincentrussell.validation.type;

public interface TypeDeterminer<T> {

    /**
     * get the type from an object.
     *
     * @param object the object to get tye type from.
     * @return the type from the object.
     */
    String getType(T object);
}
