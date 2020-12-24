# pojo-validation [![Maven Central](https://img.shields.io/maven-central/v/com.github.vincentrussell/pojo-validation.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.vincentrussell%22%20AND%20a:%22pojo-validation%22) [![Build Status](https://travis-ci.org/vincentrussell/pojo-validation.svg?branch=master)](https://travis-ci.org/vincentrussell/pojo-validation)

pojo-validation helps you validate pojo via annotations.  Additional validators can be provided to the ValidationService as needed.

## Maven

Add a dependency to `com.github.vincentrussell:pojo-validation:1.0`.

```
<dependency>
   <groupId>com.github.vincentrussell</groupId>
   <artifactId>pojo-validation</artifactId>
   <version>1.0</version>
</dependency>
```

## Requirements
- JDK 1.8 or higher

## Running it from Java

The ValidationService uses [reflections](https://github.com/ronmamo/reflections) to scan the code for the @Validation
annotations so that the objects can be validated later when you call the validate method.  This will return a
ValidationResponse object that shows the fields that have validation errors.

The packages where to scan for the field annotations should be specified.  One or more packages can be specified.

```
   ValidationService validationService = new ValidationService("some.package", "some.other.package");
   validationService.addValidator(notNullValidator);
   SimpleObject object = new SimpleObject();
   ValidationResponse validationResponse = validationService.validate(object);
```

### Annotating a field for Validation

The validators value for the @Validation annotation should match to the name of validators added to the ValidatorService.
The name for the validator can be specified by returning the name via the getName method on the Validator object.
The errorMessage will end up in the ValidationError object that is returned from the validate method.

```
@Validation(validators = "notNull", errorMessage = "you must provide a value for field1")
private String field1;
```

### Custom Validators

```
 Validator notNullValidator = new Validator() {
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
        
  ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
  validationService.addValidator(notNullValidator);        
```

```
  @Validation(validators = "notNull", errorMessage = "you must provide a value for field1")
  private String field1;
```

### Custom Validators that receive the Main Object (the object that is passed in for validation)

Sometimes you need to consider other fields or other properties of the main object in order to validate a particular field.
You can achieve this with a ValidatorWithMainObject.

```
    public static class StringNotNullMainObject implements ValidatorWithMainObject<String, SimpleObjectWithTypedValidatorMainObject> {


        public static final String BOOM = "BOOM";
        private SimpleObjectWithTypedValidatorMainObject mainObject = null;

        public SimpleObjectWithTypedValidatorMainObject getMainObject() {
            return mainObject;
        }

        @Override
        public String getName() {
            return "stringNotEqualToBOOM";
        }

        @Override
        public ValidationError validate(String object, SimpleObjectWithTypedValidatorMainObject mainObject) {
            this.mainObject = mainObject;
            if (BOOM.equals(object)) {
                throw new IllegalArgumentException("string is equal to BOOM");
            }
            return  null;
        }
    }
           
  ValidationService validationService = new ValidationService(TEST_CLASSES_PACKAGE);
  validationService.addValidator(new StringNotNullMainObject());      
  
  ValidationResponse validationResponse = validatorService.validate(new StringNotNullMainObject())  
```

```
  @Validation(validators = "stringNotEqualToBOOM", errorMessage = "you must provide a value for string field1")
  private String field1;
```

### Default Validators

#### @After

Validate that a date value is after the specified date.

Supported types are:
- ZonedDateTime
- LocalDateTime
- Date
- Calendar
- Timestamp
- LocalDate
- Long

Example:
```
  @After(format = "E, d MMM yyyy HH:mm:ss z", dateTime = "Mon, 1 Apr 2019 11:05:30 GMT")
  private Date date;
```
The default format value is: yyyy-MM-dd'T'HH:mm'Z'

#### @Before

Validate that a date value is before the specified date.

Supported types are:
- ZonedDateTime
- LocalDateTime
- Date
- Calendar
- Timestamp
- LocalDate
- Long

Example:
```
  @Before(format = "E, d MMM yyyy HH:mm:ss z", dateTime = "Mon, 1 Apr 2019 11:05:30 GMT")
  private Date date;
```
The default format value is: yyyy-MM-dd'T'HH:mm'Z'

#### @Bool

If the field is a boolean, assures that value matches to provided value; otherwise returns null.

Example:
```
  @Bool(value = false)
  private Boolean booleanField;
```
The default format value is: true

#### @DecimalMax

The field value must be a number whose value must be lower or equal to the specified maximum.

Example:
```
   @DecimalMax("1.23E+3")
   private BigInteger bigIntegerValue;
```

Supported Types:
- BigDecimal
- BigInteger
- byte, double, float, int, long, short and their respective wrappers or any subclass of Number

#### @DecimalMin

The field value must be a number whose value must be higher or equal to the specified maximum.

Example:
```
   @DecimalMin("1.23E+3")
   private BigInteger bigIntegerValue;
```

Supported Types:
- BigDecimal
- BigInteger
- byte, double, float, int, long, short and their respective wrappers or any subclass of Number

#### @Max

The field value must be a number whose value must be lower or equal to the specified maximum.

Example:
```
    @Max(5L)
    private long maxValue;
```

Supported Types:
- BigDecimal
- BigInteger
- byte, double, float, int, long, short and their respective wrappers or any subclass of Number

#### @Min

The field value must be a number whose value must be higher or equal to the specified maximum.

Example:
```
    @Min(5L)
    private long minValue;
```

Supported Types:
- BigDecimal
- BigInteger
- byte, double, float, int, long, short and their respective wrappers or any subclass of Number

#### @NotEmpty

Validates that the property is not null or empty.

Supported types are:
- CharSequence (length of character sequence is evaluated)
- Collection (collection size is evaluated)
- Map (map size is evaluated)
- Array (array length is evaluated)

Example:
```
  @NotEmpty
  private List<String> values;
```

#### @NotNull

Validates that the property is not null.

Example:
```
  @NotNull
  private String notNotValue;
```

#### @Null

Validates that the property is null.

Example:
```
  @NotNull
  private String notNotValue;
```

#### @Past

Validate that a date value is before "now".  "Now" is calculated at runtime.

Supported types are:
- ZonedDateTime
- LocalDateTime
- Date
- Calendar
- Timestamp
- LocalDate
- Long

Example:
```
  @Past
  private Date pastValue;
```

#### @Future

Validate that a date value is after "now".  "Now" is calculated at runtime.

Supported types are:
- ZonedDateTime
- LocalDateTime
- Date
- Calendar
- Timestamp
- LocalDate
- Long

Example:
```
  @Past
  private Date futureValue;
```

#### @Regex

The annotated element must be a string and matches the provided regex.

Example:
```
  @Regex(regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", flags = 0)
  private String regexValue;
```

#### @Size

The annotated element size must be between the specified boundaries (included).
Supported types are:
- CharSequence (length of character sequence is evaluated)
- Collection (collection size is evaluated)
- Map (map size is evaluated)
- Array (array length is evaluated)

Example:
```
   @Size(min = 1, max = 20)
   private String sizeValue;
```

### Validators only for certain "types"

Use a TypeDeterminer.  The string that is returned wound need to match types field on the @Validation annotation in
order get validated.  Otherwise, the validation would be skipped.

```
  public class ObjectWithTypeTypeDeterminer implements TypeDeterminer<ObjectWithType> {
        @Override
        public String getType(ObjectWithType object) {
            return object.getType();
        }
    }  
```

Add the TypeDeterminer to the ValidationService.  Notice that the TypeDelimiter only applies to a particular class.
```
    validationService.addTypeDeterminer(ObjectWithType.class, new ObjectWithTypeTypeDeterminer());
```

Specify the type of the "main object" that the Validator should only apply to.  The main object is the object that you
call validate on.
```
    @Validation(validators = "notNull", types = "typeToValidate")
    private String field1;
```


### Error messages

Specify the error message in the annotation which end up in the ValidationError object.
```
    @Validation(validators = "notNull", errorMessage = "field1 is null")
    private String field1;
```


### Validation Response

The ValidationResponse object is used to determine if the object has passed validation.  The ValidationResponse will be
valid if there are no ValidationErrors.
```
   ValidationResponse validationResponse = validationService.validate(object);
   validationResponse.isValid();
```

Get all the Validation errors.
```
   List<ValidationError> validationErrors = validationReponse.getValidationErrors();
```

Find the validation errors for a particular field.
```
List<ValidationError> validationErrors = validationResponse.findValidationErrorsForField("field1")
```

### PathAlias

The PathAlias is used rename the field that is in the ValidationResponse or the patch along the way.

If there was a failure on a field1 on the SimpleObject in the example below then the field in the ValidationResponse would be
"AliasedSimpleObjects.field1".
```
    @PathAlias("AliasedSimpleObjects")
    private List<SimpleObject> simpleObjects;
```