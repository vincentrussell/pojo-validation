package com.github.vincentrussell.validation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ReflectionUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {

    }

    /**
     * Get the value from an annotation.
     *
     * @param annotatedElement  the field or method that has the annotation
     * @param annotationClass the annotation like {@link com.github.vincentrussell.validation.annotation.NotNull}
     * @param methodName the method name on the annotation
     * @param <T> the type that is returned by invoking the method.
     * @return the value from invoking the method on this {@link java.lang.annotation.Annotation} or null.
     */
    public static <T> T getAnnotationValue(final AnnotatedElement annotatedElement,
                                           final Class annotationClass, final String methodName) {
        Annotation annotation = annotatedElement.getAnnotation(annotationClass);

        if (annotation != null) {
            try {
                Method method = annotation.getClass().getMethod(methodName);
                return (T) method.invoke(annotation);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOGGER.warn(e.getMessage(), e);
                return null;
            }
        }
        return null;
    }

    /**
     * Get a value for a field.
     *
     * @param field the field to access
     * @param object the object that contains the field
     * @return the value at that field.
     * @throws IllegalAccessException if the field can not be accessed
     */
    public static Object getValue(final Field field, final Object object) throws IllegalAccessException {
        if (object == null) {
            return null;
        }
        makeAccessible(field);
        return field.get(object);
    }

    private static void makeAccessible(final Field field) {
        if ((!Modifier.isPublic(field.getModifiers())
                || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

}
