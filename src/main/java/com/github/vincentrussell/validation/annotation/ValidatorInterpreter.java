package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.ValidationService;
import com.github.vincentrussell.validation.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * This class is used to instantiate validators based on the annotations.
 */
public final class ValidatorInterpreter {

    private static Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    private final Class<com.github.vincentrussell.validation.Validator> validatorClass;
    private final boolean isOnlyDefaultConstructor;
    private final LinkedHashMap<String, Parameter> parameterMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, Object> parameterValueMap = new LinkedHashMap<>();
    private final String validatorName;

    /**
     * Default constructor.
     *
     * @param customValidationAnnotation
     * @param fieldValidation
     * @param field
     */
    public ValidatorInterpreter(final Class customValidationAnnotation,
                                final Annotation fieldValidation, final Field field) {

        com.github.vincentrussell.validation.annotation.Validator validator =
                (com.github.vincentrussell.validation.annotation.Validator)
                        customValidationAnnotation.getAnnotation(
                                com.github.vincentrussell.validation.annotation.Validator.class);

        validatorClass = (Class<com.github.vincentrussell.validation.Validator>) validator.validatedBy();

        if (StringUtils.isEmpty(validator.name())) {
            LOGGER.error("validator {} does not have a name.", validator.getClass().getName());
            throw new IllegalArgumentException(String.format("validator %s does not have a name.",
                    validator.getClass().getName()));
        }

        isOnlyDefaultConstructor = isOnlyDefaultConstructor();

        if (isOnlyDefaultConstructor) {
            validatorName = validator.name();
        } else {
            validatorName = processConstructorsWithMultipleParameters(fieldValidation, field, validator);
        }

    }

    private String processConstructorsWithMultipleParameters(final Annotation fieldValidation,
                                                             final Field field, final Validator validator) {
        long nonDefaultConstructorCount = Arrays.stream(validatorClass.getConstructors()).sequential().filter(
                constructor -> constructor.getParameterCount() > 1).count();

        if (nonDefaultConstructorCount > 1) {
            LOGGER.error("there is more than one default constructor for {}", validatorClass);
            throw new IllegalArgumentException(String.format("there is more than one default constructor for %s",
                    validatorClass));
        }

        Parameter[] parameters = validatorClass.getConstructors()[0].getParameters();
        for (Parameter parameter : parameters) {
            processParameter(fieldValidation, field, parameters, parameter);
        }


        return parameterValueMap.entrySet().stream().reduce(
                new StringBuilder(validator.name() + "_"),
                (stringBuilder, stringObjectEntry) -> stringBuilder.append(stringObjectEntry.getKey())
                        .append("_").append(stringObjectEntry.getValue()),
                (stringBuilder, stringBuilder2) -> stringBuilder.append(stringBuilder2.toString())).toString();
    }

    private void processParameter(final Annotation fieldValidation, final Field field,
                                  final Parameter[] parameters, final Parameter parameter) {
        String name = ReflectionUtils.getAnnotationValue(parameter, ConstructorParam.class, "value");
        Object value = ReflectionUtils.getAnnotationValue(field, fieldValidation.annotationType(), name);

        if (value == null && parameters.length == 1) {
            value = ReflectionUtils.getAnnotationValue(field, fieldValidation.annotationType(), "value");
        }

        if (name == null) {
            throw new IllegalArgumentException("name is not found");
        }
        parameterMap.put(name, parameter);
        parameterValueMap.put(name, value);
    }

    /**
     * Get the same of the validator name.
     *
     * @return the name of the validator.
     */
    public String getValidatorName() {
        return validatorName;
    }

    private boolean isOnlyDefaultConstructor() {
        long constructorCount = validatorClass.getConstructors().length;
        long defaultConstructorCount = Arrays.stream(validatorClass.getConstructors()).sequential().filter(
                constructor -> constructor.getParameterCount() == 0).count();


        if (constructorCount == defaultConstructorCount) {
            return true;
        }
        return false;
    }


    /**
     * instantiate this validator, even if it doesn't have a default constructor.
     *
     * @return the {@link com.github.vincentrussell.validation.Validator}
     */
    public com.github.vincentrussell.validation.Validator instantiate() {
        if (isOnlyDefaultConstructor) {
            try {
                return validatorClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(e.getMessage(), e);
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        } else {
            try {
                return (com.github.vincentrussell.validation.Validator) validatorClass.getConstructors()[0]
                        .newInstance(parameterValueMap.values().toArray(new Object[0]));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }

}
