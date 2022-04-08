package com.github.vincentrussell.validation;

import com.github.vincentrussell.validation.annotation.After;
import com.github.vincentrussell.validation.annotation.Before;
import com.github.vincentrussell.validation.annotation.Bool;
import com.github.vincentrussell.validation.annotation.DecimalMax;
import com.github.vincentrussell.validation.annotation.DecimalMin;
import com.github.vincentrussell.validation.annotation.Future;
import com.github.vincentrussell.validation.annotation.Max;
import com.github.vincentrussell.validation.annotation.Min;
import com.github.vincentrussell.validation.annotation.NotEmpty;
import com.github.vincentrussell.validation.annotation.NotNull;
import com.github.vincentrussell.validation.annotation.Null;
import com.github.vincentrussell.validation.annotation.Past;
import com.github.vincentrussell.validation.annotation.PathAlias;
import com.github.vincentrussell.validation.annotation.Regex;
import com.github.vincentrussell.validation.annotation.Required;
import com.github.vincentrussell.validation.annotation.Size;
import com.github.vincentrussell.validation.annotation.Validation;
import com.github.vincentrussell.validation.annotation.ValidatorInterpreter;
import com.github.vincentrussell.validation.defaultValidators.AfterValidator;
import com.github.vincentrussell.validation.scanner.ReverseConnectionScanner;
import com.github.vincentrussell.validation.tree.TreeNode;
import com.github.vincentrussell.validation.type.TypeDeterminer;
import com.github.vincentrussell.validation.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * The main class that is responsible for validation.
 */
public final class ValidationService {

    private static Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);

    private final Reflections reflections;
    private final Set<Field> fieldsMarkedWithValidation = new HashSet<>();
    private final Set<Class> classesToValidate = new HashSet<>();
    private final Map<Class, Set<Field>> classToFieldsMap = new HashMap<>();
    private final Map<Field, Set<String>> fieldToValidatorNames = new HashMap<>();
    private final Map<Field, Set<String>> fieldToTypes = new HashMap<>();
    private final Map<String, Validator> validatorNameToValidator = new HashMap<>();
    private final Map<Field, String> fieldToErrorMessageMap = new HashMap<>();
    private final Map<Field, String> fieldToAliasMap = new HashMap<>();
    private final Map<Class, TypeDeterminer> classToTypeDeterminerMap = new HashMap<>();

    /**
     * Create a {@link com.github.vincentrussell.validation.ValidationService}.
     *
     * @param params the classes or packages (as a string) that should be considered for validation.
     */
    public ValidationService(final Object... params) {
        final Set<Object> paramsSet = params != null ? new HashSet<>(Arrays.asList(params)) : new HashSet<>();
        paramsSet.add(AfterValidator.class.getPackage().getName());
        paramsSet.add(After.class.getPackage().getName());
        paramsSet.addAll(Arrays.asList(After.class, Before.class, Future.class, Past.class, NotNull.class, Null.class,
                Size.class, NotEmpty.class, Regex.class, Max.class, Min.class, DecimalMax.class, DecimalMin.class,
                Bool.class, Regex.class, Size.class, Required.class));
        final ReverseConnectionScanner reverseConnectionScanner = new ReverseConnectionScanner();
        this.reflections = new Reflections(ConfigurationBuilder.build(paramsSet.toArray(new Object[0]))
                .setScanners(new FieldAnnotationsScanner(), reverseConnectionScanner));
        for (Object param : paramsSet) {
            if (Class.class.isInstance(param) && ((Class) param).isAnnotation()
                    && ((Class) param).isAnnotationPresent(
                            com.github.vincentrussell.validation.annotation.Validator.class)) {
                addDefaultValidator(((Class) param));
            }
        }
        try {
            findAndStoreValidationAnnotations();
            findAndStorePathAliasAnnotations();
        } catch (ReflectionsException e) {
            if (e.getMessage().contains("Scanner FieldAnnotationsScanner was not configured")) {
                throw new IllegalArgumentException("The validationService couldn't find any annotations."
                        + "This means that you didn't pass any classes with annotations into the "
                        + "constructor of the service.  You can also pass in the package or packages where the classes"
                        + "can be found as strings.", e);
            }
        }
        final TreeNode<Object> rootTreeNode = reverseConnectionScanner.getRootTreeNode();
        recurseTreeAndRepopulateHelperMaps(new LinkedList<>(), new HashSet<>(
                        rootTreeNode.getChildren()), rootTreeNode);
    }

    private void addDefaultValidator(final Class... classes) {
        for (Class clazz : classes) {
            if (!clazz.isAnnotation()
                    || !clazz.isAnnotationPresent(com.github.vincentrussell.validation.annotation.Validator.class)) {
                LOGGER.error("class {} is not an annotation with the Validator annotation.", clazz.getName());
                throw new IllegalArgumentException(String.format("class %s is not an annotation"
                        + "with the Validator annotation.", clazz.getName()));
            }

            Set<Field> fieldsMarkedWithAnnotation = reflections.getFieldsAnnotatedWith(clazz);
            fieldsMarkedWithAnnotation.forEach(field -> {
                fieldsMarkedWithValidation.add(field);
                addFieldToClassToFieldMap(field, clazz);
            });
        }
    }

    private void recurseTreeAndRepopulateHelperMaps(final LinkedList<TreeNode<Object>> classStack,
                                                    final Set<TreeNode<Object>> rootNotesToNotRepeat,
                                                    final TreeNode<Object> thisNode) {
        final List<TreeNode<Object>> list = new ArrayList<>(thisNode.getChildren());
        //do the deepest paths first to allow detection of circular paths
        Collections.sort(list, (o1, o2) -> Long.compare(o2.getDepth(), o1.getDepth()));

        for (TreeNode<Object> treeNode : list) {
            classStack.add(treeNode);
            if (rootNotesToNotRepeat.contains(treeNode)) {
                List<TreeNode<Object>> treeNodeInSTack = classStack.stream()
                        .filter(objectTreeNode -> objectTreeNode.equals(treeNode)).collect(Collectors.toList());
                if (treeNodeInSTack.size() > 1) {
                    //circular path detected...abort
                    classStack.pop();
                    continue;
                }
            }
            recurseTreeAndRepopulateHelperMaps(classStack, rootNotesToNotRepeat, treeNode);
        }


        if (classStack.isEmpty()) {
            return;
        }

        if (Field.class.isInstance(classStack.peekLast().getData())) {
            addStackToValidate(classStack);
        }

        if (!classStack.isEmpty()) {
            classStack.pollLast();
        }

        if (!classStack.isEmpty() && Field.class.isInstance(classStack.peekLast().getData())) {
            addStackToValidate(classStack);
        }
    }

    private void addStackToValidate(final LinkedList<TreeNode<Object>> classStack) {
        LinkedList<TreeNode<Object>> copy = new LinkedList<>(classStack);
        final Field field = (Field) copy.pollLast().getData();

        if (!fieldsMarkedWithValidation.contains(field)) {
            return;
        }

        final List<TreeNode<Object>> list = new ArrayList<>(copy);
        Collections.reverse(list);

        for (TreeNode<Object> treeNode : list) {
            if (Class.class.isInstance(treeNode.getData())) {
                classesToValidate.add((Class) treeNode.getData());
            } else if (Field.class.isInstance(treeNode.getData())) {
                classToFieldsMap.computeIfAbsent(((Field) treeNode.getData()).getDeclaringClass(),
                        aClass -> new HashSet<>()).add((Field) treeNode.getData());
            }
        }
    }

    private void findAndStorePathAliasAnnotations() {
        reflections.getFieldsAnnotatedWith(PathAlias.class).forEach(
                field -> {
                    if (field.isAnnotationPresent(PathAlias.class)) {
                        PathAlias pathAlias = field.getAnnotation(PathAlias.class);
                        if (pathAlias.value() != null) {
                            LOGGER.debug("found path alias field={}, pathAlias={}", field, pathAlias.value());
                            fieldToAliasMap.put(field, pathAlias.value());
                        }
                    }
                }
        );
    }

    private void findAndStoreValidationAnnotations() {
        fieldsMarkedWithValidation.addAll(reflections.getFieldsAnnotatedWith(Validation.class));
        fieldsMarkedWithValidation.forEach(field -> {
            addFieldToClassToFieldMap(field, null);
        });
    }

    private void addFieldToClassToFieldMap(final Field field, final Class customValidationAnnotation) {
        Class<?> declaringClass = field.getDeclaringClass();
        LOGGER.debug("adding class to validate {}", declaringClass.getName());
        LOGGER.debug("adding field to validate {}", field);
        classesToValidate.add(declaringClass);
        classToFieldsMap.computeIfAbsent(declaringClass, aClass -> new HashSet<>()).add(field);
        if (field.isAnnotationPresent(Validation.class)) {
            processValidationAnnotation(field);
        }
        if (customValidationAnnotation != null && field.isAnnotationPresent(customValidationAnnotation)) {
            processDefaultValidationAnnotation(field, customValidationAnnotation);
        }
    }

    private void processDefaultValidationAnnotation(final Field field, final Class customValidationAnnotation) {

        Annotation annotation = field.getAnnotation(customValidationAnnotation);

        ValidatorInterpreter validatorInterpreter = new ValidatorInterpreter(
                customValidationAnnotation, annotation, field);

        if (!validatorNameToValidator.containsKey(validatorInterpreter.getValidatorName())) {
            Validator validatorObj = validatorInterpreter.instantiate();
            addValidator(validatorInterpreter.getValidatorName(), validatorObj);
        }

        LOGGER.debug("found validators field={}, validators={}", field, validatorInterpreter.getValidatorName());
        fieldToValidatorNames.computeIfAbsent(field, aClass -> new HashSet<>())
                .addAll(Arrays.asList(validatorInterpreter.getValidatorName()));

        String errorMessage = ReflectionUtils.getAnnotationValue(field, annotation.annotationType(), "errorMessage");
        if (!StringUtils.isEmpty(errorMessage)) {
            LOGGER.debug("found errorMessage field={}, errorMessage={}", field, errorMessage);
            fieldToErrorMessageMap.put(field, errorMessage);
        }
        String[] types = ReflectionUtils.getAnnotationValue(field, annotation.annotationType(), "types");
        if (types != null) {
            Set<String> typesSet = Arrays.asList(types).stream()
                    .filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toSet());
            if (!typesSet.isEmpty()) {
                LOGGER.debug("found types to validate field={}, types={}", field, types);
                fieldToTypes.computeIfAbsent(field, aClass -> new HashSet<>())
                        .addAll(typesSet);
            }
        }
    }

    private void processValidationAnnotation(final Field field) {
        Validation validation = field.getAnnotation(Validation.class);
        String[] validators = validation.validators();
        if (validators != null) {
            LOGGER.debug("found validators field={}, validators={}", field, validators);
            fieldToValidatorNames.computeIfAbsent(field, aClass -> new HashSet<>())
                    .addAll(Arrays.asList(validators));
        }
        String errorMessage = validation.errorMessage();
        if (!StringUtils.isEmpty(errorMessage)) {
            LOGGER.debug("found errorMessage field={}, errorMessage={}", field, errorMessage);
            fieldToErrorMessageMap.put(field, errorMessage);
        }
        String[] types = validation.types();
        if (types != null) {
            Set<String> typesSet = Arrays.asList(types).stream()
                    .filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toSet());
            if (!typesSet.isEmpty()) {
                LOGGER.debug("found types to validate field={}, types={}", field, types);
                fieldToTypes.computeIfAbsent(field, aClass -> new HashSet<>())
                        .addAll(typesSet);
            }
        }
    }

    /**
     * add a {@link com.github.vincentrussell.validation.Validator}.
     *
     * @param key       the key for the validator
     * @param validator the {@link com.github.vincentrussell.validation.Validator}
     */
    private void addValidator(final String key, final Validator validator) {
        notNull(validator, "the passed in validator is null ");
        notNull(key, "there must be a name for this validator " + validator.toString());
        LOGGER.info("addValidator key={}, validator={}", key, validator);
        validatorNameToValidator.put(key, validator);
    }

    /**
     * add a {@link com.github.vincentrussell.validation.Validator}.
     * @param validator the {@link com.github.vincentrussell.validation.Validator}
     */
    public void addValidator(final Validator validator) {
        notNull(validator, "the passed in validator is null ");
        String name = validator.getName();
        notNull(name, "there must be a name for this validator " + validator.toString());
        LOGGER.info("addValidator key={}, validator={}", name, validator);
        validatorNameToValidator.put(name, validator);
    }

    /**
     * add a {@link com.github.vincentrussell.validation.type.TypeDeterminer} for a particular class.
     *
     * @param objectWithTypeClass main object class
     * @param typeDeterminer the class that is used to determine the type for that class
     * @param <T> the type of the main class.  Makes sure that tye {@link TypeDeterminer} corresponds to the class
     */
    public <T> void addTypeDeterminer(final Class<T> objectWithTypeClass, final TypeDeterminer<T> typeDeterminer) {
        LOGGER.info("addTypeDeterminer objectWithTypeClass={}, typeDeterminer={}", objectWithTypeClass, typeDeterminer);
        classToTypeDeterminerMap.put(objectWithTypeClass, typeDeterminer);
    }

    /**
     * add multiple {@link TypeDeterminer}s  in the form of a map.
     *
     * @param map the map of main class to {@link com.github.vincentrussell.validation.type.TypeDeterminer}
     * @param <T> the type of the main class.  Makes sure that tye {@link TypeDeterminer} corresponds to the class
     */
    public <T> void addTypeDeterminers(final Map<? extends Class<T>, ? extends TypeDeterminer<T>> map) {
        classToTypeDeterminerMap.forEach((objectWithTypeClass, typeDeterminer)
                -> LOGGER.info("addTypeDeterminer objectWithTypeClass={}, typeDeterminer={}",
                objectWithTypeClass, typeDeterminer));
        classToTypeDeterminerMap.putAll(map);
    }

    /**
     * add multiple validators in the form of a map.
     *
     * @param map the map of key to {@link com.github.vincentrussell.validation.Validator}s.
     */
    public void addValidators(final Map<? extends String, ? extends Validator> map) {
        validatorNameToValidator.forEach((key, validator) ->
                LOGGER.info("addValidator key={}, validator={}", key, validator));
        validatorNameToValidator.putAll(map);
    }


    /**
     * Validate the pojo based on the provided {@link com.github.vincentrussell.validation.Validator}s.  And the
     * {@link com.github.vincentrussell.validation.annotation.Validation} annotations.
     *
     * @param object the object that can be validate.  All fields will be checked recursively for validation.
     * @return the {@link com.github.vincentrussell.validation.ValidationResponse} that will contain all of the
     * {@link com.github.vincentrussell.validation.ValidationError}s.
     */
    public ValidationResponse validate(final Object object) {
        final Stack<String> pathStack = new Stack<>();
        final ValidationResponse validationResponse = new ValidationResponse();
        if (classesToValidate.contains(object.getClass())) {
            TypeDeterminer typeDeterminer = classToTypeDeterminerMap.get(object.getClass());
            LOGGER.debug("found typeDeterminer {} for class ", typeDeterminer, object.getClass());
            doValidate(object, validationResponse, pathStack, object, object.getClass(),
                    (typeDeterminer != null) ? typeDeterminer.getType(object) : null);
        }
        return validationResponse;
    }

    private void doValidate(final Object mainObject, final ValidationResponse validationResponse,
                            final Stack<String> pathStack, final Object object, final Class<?> type,
                            final String determinedType) {
        if (Iterable.class.isInstance(object)) {
            doValidateIteratorObject(mainObject, validationResponse, pathStack, (Iterable) object, determinedType);
            return;
        } else if (Map.class.isInstance(object)) {
            doValidateMapObject(mainObject, validationResponse, pathStack, (Map) object, determinedType);
            return;
        }
        for (Field field : firstNonNull(classToFieldsMap.get(type), Collections.<Field>emptyList())) {
            pathStack.add(firstNonNull(fieldToAliasMap.get(field), field.getName()));
            doValidateField(mainObject, validationResponse, pathStack, object, field, determinedType);
            pathStack.pop();
        }
    }

    private void doValidateField(final Object mainObject, final ValidationResponse validationResponse,
                                 final Stack<String> pathStack,
                                 final Object object, final Field field, final String determinedType) {
        Collection<String> validators = firstNonNull(
                fieldToValidatorNames.get(field), Collections.emptyList());
        if (validators.isEmpty()) {
            doValidateWhenNoValidatorsFound(mainObject, validationResponse, pathStack, object, field, determinedType);
            return;
        }

        for (String validatorName : validators) {
            Validator validator = validatorNameToValidator.get(validatorName);
            if (validator != null) {
                doValidateAndAddErrorToValidationResponse(mainObject, validationResponse, pathStack, object,
                        field, validator, determinedType);
            }
        }
    }

    private void doValidateWhenNoValidatorsFound(final Object mainObject, final ValidationResponse validationResponse,
                                                 final Stack<String> pathStack,
                                                 final Object object, final Field field,
                                                 final String determinedType) {
        Object value = null;
        try {
            value = ReflectionUtils.getValue(field, object);
            if (value != null) {
                doValidate(mainObject, validationResponse, pathStack, value, field.getType(), determinedType);
            }
        } catch (IllegalAccessException e) {
            LOGGER.error(String.format("error validating field=%s, value=%s", field, value), e.getMessage());
            validationResponse.addError(new ValidationError(pathStack, value, e));
        }
    }

    private void doValidateAndAddErrorToValidationResponse(final Object mainObject,
                                                           final ValidationResponse validationResponse,
                                                           final Stack<String> pathStack, final Object object,
                                                           final Field field, final Validator validator,
                                                           final String determinedType) {
        Object value = null;
        try {
            Set<String> types = determinedType != null ? fieldToTypes.get(field) : Collections.emptySet();
            if (!types.isEmpty() && !types.contains(determinedType)) {
                return;
            }
            value = ReflectionUtils.getValue(field, object);
            LOGGER.debug("validating field={}, validator={}, value={}", field, validator, value);
            ValidationError validationError = null;
            if (ValidatorWithMainObject.class.isInstance(validator)) {
                validationError = ((ValidatorWithMainObject) validator).validate(value, mainObject);
            } else {
                validationError = validator.validate(value);
            }
            LOGGER.debug("validating field={}, validator={}, value={}, validationError={}", field,
                    validator, value, validationError);
            if (validationError != null) {
                addValidationErrorToValidationResponse(validationResponse, pathStack, field, value, validationError);
            }
        } catch (Throwable e) {
            LOGGER.error(String.format("error validating field=%s, validator=%s, value=%s", field,
                    validator, value), e.getMessage());
            ValidationError validationError = new ValidationError(pathStack, value, e);
            validationError.setValue(value);
            validationResponse.addError(validationError);
        }
    }

    private void addValidationErrorToValidationResponse(final ValidationResponse validationResponse,
                                                        final Stack<String> pathStack, final Field field,
                                                        final Object value, final ValidationError validationError) {
        String errorMessage = fieldToErrorMessageMap.get(field);

        if (!StringUtils.isEmpty(errorMessage)) {
            validationError.setErrorMessage(errorMessage);
        }
        validationError.setValue(value);
        validationError.setField(pathStack);
        validationResponse.addError(validationError);
    }

    private void doValidateIteratorObject(final Object mainObject, final ValidationResponse validationResponse,
                                          final Stack<String> pathStack, final Iterable<Object> object,
                                          final String determinedType) {
        for (Object obj : object) {
            if (obj != null) {
                doValidate(mainObject, validationResponse, pathStack, obj, obj.getClass(), determinedType);
            }
        }
    }

    private void doValidateMapObject(final Object mainObject, final ValidationResponse validationResponse,
                                     final Stack<String> pathStack, final Map object,
                                     final String determinedType) {
        for (Object entry : object.entrySet()) {
            if (Map.Entry.class.isInstance(entry)) {
                Map.Entry thisEntry = (Map.Entry) entry;
                if (thisEntry.getKey() != null) {
                    doValidate(mainObject, validationResponse, pathStack, thisEntry.getKey(),
                            thisEntry.getKey().getClass(), determinedType);
                }
                if (thisEntry.getValue() != null) {
                    doValidate(mainObject, validationResponse, pathStack, thisEntry.getValue(),
                            thisEntry.getValue().getClass(), determinedType);
                }
            }
        }
    }
}
