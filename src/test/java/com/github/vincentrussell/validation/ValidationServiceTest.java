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
import com.github.vincentrussell.validation.annotation.Past;
import com.github.vincentrussell.validation.annotation.Regex;
import com.github.vincentrussell.validation.annotation.Size;
import com.github.vincentrussell.validation.testClasses.deep.ObjectWithDeepNesting;
import com.github.vincentrussell.validation.testClasses.deepWithCircular.ObjectWithDeepNestingWithCircular;
import com.github.vincentrussell.validation.testClasses.defaultValidators.SimpleObjectDefaultValidators;
import com.github.vincentrussell.validation.testClasses.nestedCollections.ObjectWithNestedCollectionObjects;
import com.github.vincentrussell.validation.testClasses.nestedMaps.ObjectWithNestedMapObjects;
import com.github.vincentrussell.validation.testClasses.nestedObjects.ObjectWithNestedObjects;
import com.github.vincentrussell.validation.testClasses.simpleMultipleFields.SimpleObjectWithMultipleFields;
import com.github.vincentrussell.validation.testClasses.typedValidator.SimpleObjectWithTypedValidator;
import com.github.vincentrussell.validation.testClasses.typedWithMainObjectValidator.SimpleObjectWithTypedValidatorMainObject;
import com.github.vincentrussell.validation.testClasses.withType.ObjectWithType;
import com.github.vincentrussell.validation.testClasses.simple.SimpleObject;
import com.github.vincentrussell.validation.testClasses.SimpleObjectWithNonEmptyValidation;
import com.github.vincentrussell.validation.type.TypeDeterminer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ValidationServiceTest {

    public static final String EMPTY_STRING = "";
    public static final String TEST_CLASSES_PACKAGE = "com.github.vincentrussell.validation.testClasses";
    private Validator notNullValidator;
    private Validator emptyStringValidator;

    @org.junit.Before
    public void createValidator() {
        notNullValidator = new Validator() {
            @Override
            public String getName() {
                return "notNull";
            }

            @Override
            public ValidationError validate(Object object) {
                if (object == null) {
                    return new ValidationError(new NullPointerException("the field is null"));
                }
                return null;
            }
        };
        emptyStringValidator = new Validator() {
            public String getName() {
                return "notEmpty";
            }

            @Override
            public ValidationError validate(Object object) {
                if (StringUtils.isEmpty(object.toString())) {
                    throw new IllegalArgumentException("value is a nonEmpty String");
                }
                return null;
            }
        };
    }


    @Test
    public void testWithValidatorOnField() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        SimpleObject object = new SimpleObject();
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.setField1("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }



    @Test
    public void testWithValidatorOnMultipleFields() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        SimpleObjectWithMultipleFields object = new SimpleObjectWithMultipleFields();
        ValidationResponse validationResponse = validationService.validate(object);
        assertEquals("field2", validationResponse.findValidationErrorsForField("field2").get(0).getField());
        assertEquals("field1", validationResponse.findValidationErrorsForField("field1").get(0).getField());
        assertFalse(validationResponse.isValid());
        object.setField1("not null");
        validationResponse = validationService.validate(object);
        assertEquals("field2", validationResponse.getValidationErrors().get(0).getField());
        assertFalse(validationResponse.isValid());
        object.setField2("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }


    @Test
    public void testWithValidatorOnNestedField() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        ObjectWithNestedObjects object = new ObjectWithNestedObjects();
        ValidationResponse validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
        object.setSimpleObject1(new SimpleObject());
        validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.getSimpleObject1().setField1("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void testWithValidatorOnNestedCollectionField() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        ObjectWithNestedCollectionObjects object = new ObjectWithNestedCollectionObjects();
        ValidationResponse validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
        object.setSimpleObjects(Arrays.asList(new SimpleObject()));
        validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.getSimpleObjects().get(0).setField1("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void testFieldNameInValidationErrorsIsBasedOnStackAndPathAliasIsUsed() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        ObjectWithNestedCollectionObjects object = new ObjectWithNestedCollectionObjects();
        object.setSimpleObjects(Arrays.asList(new SimpleObject()));
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        assertEquals("AliasedSimpleObjects.field1", validationResponse.getValidationErrors().get(0).getField());
    }

    @Test
    public void testPathAliasIsUsed() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        ObjectWithNestedCollectionObjects object = new ObjectWithNestedCollectionObjects();
        object.setSimpleObjects(Arrays.asList(new SimpleObject()));
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        assertEquals("AliasedSimpleObjects.field1", validationResponse.getValidationErrors().get(0).getField());
    }

    @Test
    public void testWithValidatorOnNestedMapField() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        ObjectWithNestedMapObjects object = new ObjectWithNestedMapObjects();
        ValidationResponse validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
        object.setSimpleObjectsMap(Collections.singletonMap("key", new SimpleObject()));
        validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.getSimpleObjectsMap().get("key").setField1("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void objectWithType() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        validationService.addTypeDeterminer(ObjectWithType.class, new ObjectWithTypeTypeDeterminer());
        ObjectWithType object = new ObjectWithType();
        object.setSubType(new ObjectWithType.SubType());
        object.setType("someUnvalidatedType");
        ValidationResponse validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
        object.setType("typeToValidate");
        validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        assertEquals("subType.field1 is null which is incorrect", validationResponse.findValidationErrorsForField("subType.field1").get(0).getErrorMessage());
        object.getSubType().setField1("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }


    @Test
    public void testValidatorErrorSetsValueOnValidationError() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(emptyStringValidator);
        SimpleObjectWithNonEmptyValidation object = new SimpleObjectWithNonEmptyValidation();
        object.setField1(EMPTY_STRING);
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        assertSame(EMPTY_STRING, validationResponse.getValidationErrors().get(0).getValue());
        object.setField1("fadfasf");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }


    @Test
    public void testWithValidatorOnDeepNestedField() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        ObjectWithDeepNesting object = new ObjectWithDeepNesting();
        object.setSecondClassList(Arrays.asList(new ObjectWithDeepNesting.SecondClass()));
        object.getSecondClassList().get(0).setThirdClassList(Arrays.asList(new ObjectWithDeepNesting.ThirdClass()));
        object.getSecondClassList().get(0).getThirdClassList().get(0).setForthClassList(Arrays.asList(new ObjectWithDeepNesting.ForthClass()));
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        ObjectWithDeepNesting.ForthClass forthClass = object.getSecondClassList().get(0).getThirdClassList().get(0).getForthClassList().get(0);
        forthClass.setField1("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void testWithValidatorOnDeepNestedFieldWithCircularReference() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(notNullValidator);
        ObjectWithDeepNestingWithCircular object = new ObjectWithDeepNestingWithCircular();
        object.setSecondClassList(Arrays.asList(new ObjectWithDeepNestingWithCircular.SecondClass()));
        object.getSecondClassList().get(0).setThirdClassList(Arrays.asList(new ObjectWithDeepNestingWithCircular.ThirdClass()));
        object.getSecondClassList().get(0).getThirdClassList().get(0).setForthClassList(Arrays.asList(new ObjectWithDeepNestingWithCircular.ForthClass()));
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        ObjectWithDeepNestingWithCircular.ForthClass forthClass = object.getSecondClassList().get(0).getThirdClassList().get(0).getForthClassList().get(0);
        forthClass.setField1("not null");
        validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        assertEquals(1, validationResponse.findValidationErrorsForField("secondClassList.thirdClassList.forthClassList.circular").size());
        forthClass.setCircular(new ObjectWithDeepNestingWithCircular());
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void testWithValidatorOnFieldWithTypedValidator() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(new SimpleObjectWithTypedValidator.StringNotNull());
        SimpleObjectWithTypedValidator object = new SimpleObjectWithTypedValidator();
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.setField1("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void testWithValidatorOnFieldWithTypedValidatorTypeMisMatchOnValidatorGeneric() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        validationService.addValidator(new SimpleObjectWithTypedValidator.IntegerNotNull());
        SimpleObjectWithTypedValidator object = new SimpleObjectWithTypedValidator();
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.setField2(39123);
        validationResponse = validationService.validate(object);
        assertEquals(1, validationResponse.findValidationErrorsForField("field2").size());
        assertTrue(ClassCastException.class.isInstance(validationResponse.findValidationErrorsForField("field2").get(0).getThrowable()));
    }

    @Test
    public void testWithValidatorOnFieldWithTypedValidatorTypeWithMainObject() {
        String failingString = SimpleObjectWithTypedValidatorMainObject.StringNotNullMainObject.BOOM;
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        SimpleObjectWithTypedValidatorMainObject.StringNotNullMainObject validator = new SimpleObjectWithTypedValidatorMainObject.StringNotNullMainObject();
        validationService.addValidator(validator);
        SimpleObjectWithTypedValidatorMainObject object = new SimpleObjectWithTypedValidatorMainObject();
        object.setField1(failingString);
        assertNull(validator.getMainObject());
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        assertSame(failingString, validationResponse.findValidationErrorsForField("field1").get(0).getValue());
        assertSame(object, validator.getMainObject());
        object.setField1("not null");
        validationResponse = validationService.validate(object);
        assertSame(object, validator.getMainObject());
        assertTrue(validationResponse.isValid());
    }


    @Test
    public void defaultValidation() {
        ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
        SimpleObjectDefaultValidators object = new SimpleObjectDefaultValidators();
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.setField1("not null");
        object.setField2("sdfsdafsdfsadfdsafsdfsdfsdf");
        validationResponse = validationService.validate(object);
        assertEquals("field is not in within size constraints", validationResponse.findValidationErrorsForField("field2").get(0).getErrorMessage());
        object.setField2("2342wsfs");
        validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.setRequiredField("value");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void defaultValidatorsTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
        ValidationService validationService = new ValidationService(TestObjectWithAllValidators.class);
        TestObjectWithAllValidators testObjectWithAllValidators = new TestObjectWithAllValidators();
        ValidationResponse validationResponse = validationService.validate(testObjectWithAllValidators);
        assertFalse(validationResponse.isValid());
        assertEquals(1, validationResponse.getValidationErrors().size());
        assertEquals("notNullValue is null", validationResponse.findValidationErrorsForField("notNullValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "afterDate", Date.from(ZonedDateTime
                .parse("2018-05-27 10:15:30 AM +05:30", formatter).toInstant()));
        assertEquals("afterDate isn't after specified date", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("afterDate").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "beforeDate", Date.from(ZonedDateTime
                .parse("2020-05-27 10:15:30 AM +05:30", formatter).toInstant()));
        assertEquals("beforeDate isn't before specified value", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("beforeDate").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "booleanField", Boolean.TRUE);
        assertEquals("booleanField doesn't match expected value", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("booleanField").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "decimalMaxValue", new BigDecimal("1.33E+3").toBigInteger());
        assertEquals("decimalMaxValue is too high", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("decimalMaxValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "decimalMinValue", new BigDecimal("1.11E+3").toBigInteger());
        assertEquals("decimalMinValue is too low", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("decimalMinValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "maxValue", 6L);
        assertEquals("maxValue is too high", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("maxValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "minValue", 1L);
        assertEquals("minValue is too low", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("minValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "notEmptyValue", new ArrayList<>());
        assertEquals("notEmptyValue is empty", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("notEmptyValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "pastValue", Date.from(ZonedDateTime.now(ZoneId.of("UTC")).plusYears(3).toInstant()));
        assertEquals("pastValue is not in past", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("pastValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "futureValue", Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusYears(3).toInstant()));
        assertEquals("futureValue is not in future", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("futureValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "regexValue", "some random string");
        assertEquals("regexValue doesn't match regex", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("regexValue").get(0).getErrorMessage());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "sizeValue", "                                                     ");
        assertEquals("sizeValue is out of bounds", validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("sizeValue").get(0).getErrorMessage());


        ReflectionTestUtils.setField(testObjectWithAllValidators, "notNullValue", "");
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("notNullValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "afterDate", Date.from(ZonedDateTime
                .parse("2020-05-27 10:15:30 AM +05:30", formatter).toInstant()));
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("afterDate").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "beforeDate", Date.from(ZonedDateTime
                .parse("2017-05-27 10:15:30 AM +05:30", formatter).toInstant()));
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("beforeDate").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "booleanField", Boolean.FALSE);
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("booleanField").size());

        ReflectionTestUtils.setField(testObjectWithAllValidators, "decimalMaxValue", new BigDecimal("1").toBigInteger());
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("decimalMaxValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "decimalMinValue", new BigDecimal("2.11E+3").toBigInteger());
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("decimalMinValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "maxValue", 1L);
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("maxValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "minValue", 6L);
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("minValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "notEmptyValue", Arrays.asList(""));
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("notEmptyValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "pastValue", Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusYears(3).toInstant()));
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("pastValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "futureValue", Date.from(ZonedDateTime.now(ZoneId.of("UTC")).plusYears(3).toInstant()));
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("futureValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "regexValue", "http://www.google.com");
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("regexValue").size());
        ReflectionTestUtils.setField(testObjectWithAllValidators, "sizeValue", "small string");
        assertEquals(0, validationService.validate(testObjectWithAllValidators).findValidationErrorsForField("sizeValue").size());

    }

    private static class TestObjectWithAllValidators {

        @After(format = "E, d MMM yyyy HH:mm:ss z", dateTime = "Mon, 1 Apr 2019 11:05:30 GMT", errorMessage = "afterDate isn't after specified date")
        private Date afterDate;

        @Before(format = "E, d MMM yyyy HH:mm:ss z", dateTime = "Mon, 1 Apr 2019 11:05:30 GMT", errorMessage = "beforeDate isn't before specified value")
        private Date beforeDate;

        @Bool(value = false, errorMessage = "booleanField doesn't match expected value")
        private Boolean booleanField;

        @DecimalMax(value = "1.23E+3", errorMessage = "decimalMaxValue is too high")
        private BigInteger decimalMaxValue;

        @DecimalMin(value = "1.23E+3", errorMessage = "decimalMinValue is too low")
        private BigInteger decimalMinValue;

        @Max(value = 5L, errorMessage = "maxValue is too high")
        private Long maxValue;

        @Min(value = 5L, errorMessage = "minValue is too low")
        private Long minValue;

        @NotEmpty(errorMessage = "notEmptyValue is empty")
        private List<String> notEmptyValue;

        @NotNull(errorMessage = "notNullValue is null")
        private String notNullValue;

        @Past(errorMessage = "pastValue is not in past")
        private Date pastValue;

        @Future(errorMessage = "futureValue is not in future")
        private Date futureValue;

        @Regex(regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", flags = 0,
                errorMessage = "regexValue doesn't match regex")
        private String regexValue;

        @Size(min = 1, max = 20, errorMessage = "sizeValue is out of bounds")
        private String sizeValue;

    }

    private class ObjectWithTypeTypeDeterminer implements TypeDeterminer<ObjectWithType> {
        @Override
        public String getType(ObjectWithType object) {
            return object.getType();
        }
    }
}