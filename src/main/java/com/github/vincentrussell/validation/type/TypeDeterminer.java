package com.github.vincentrussell.validation.type;

public interface TypeDeterminer<T> {

    /**
     * get the type from a main object.
     *
     * @param object the object to get tye type from.
     * @return the type from the object.
     */
    String getType(T object);
}
