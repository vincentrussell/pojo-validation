package com.github.vincentrussell.validation;

import com.github.vincentrussell.validation.defaultValidators.AfterValidator;
import com.github.vincentrussell.validation.defaultValidators.BeforeValidator;
import com.github.vincentrussell.validation.defaultValidators.BoolValidator;
import com.github.vincentrussell.validation.defaultValidators.DecimalMaxValidator;
import com.github.vincentrussell.validation.defaultValidators.DecimalMinValidator;
import com.github.vincentrussell.validation.defaultValidators.FutureValidator;
import com.github.vincentrussell.validation.defaultValidators.MaxValidator;
import com.github.vincentrussell.validation.defaultValidators.MinValidator;
import com.github.vincentrussell.validation.defaultValidators.NotEmptyValidator;
import com.github.vincentrussell.validation.defaultValidators.NotNullValidator;
import com.github.vincentrussell.validation.defaultValidators.NullValidator;
import com.github.vincentrussell.validation.defaultValidators.PastValidator;
import com.github.vincentrussell.validation.defaultValidators.RegexValidator;
import com.github.vincentrussell.validation.defaultValidators.RequiredValidator;
import com.github.vincentrussell.validation.defaultValidators.SizeValidator;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.time.DateUtils.toCalendar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ValidatorTest {


    @Test
    public void sizeValidatorStringTests() throws Exception {
       SizeValidator sizeValidator = getValidator(SizeValidator.class, -1, 3);
       assertValidationErrorThrowableMessage(() -> sizeValidator.validate("abd45"), "string length 5 didn't between required min -1 and max 3");
       assertValidationSuccess(() -> sizeValidator.validate("abc"));
       SizeValidator sizeValidator2 = getValidator(SizeValidator.class, 2, 3);
       assertValidationErrorThrowableMessage(() -> sizeValidator2.validate("a"), "string length 1 didn't between required min 2 and max 3");
    }

    @Test
    public void sizeValidatorCollectionTests() throws Exception {
        SizeValidator sizeValidator = getValidator(SizeValidator.class, -1, 3);
        assertValidationErrorThrowableMessage(() -> sizeValidator.validate(Arrays.asList("1", "2", "3", "4", "5")), "collection length 5 didn't between required min -1 and max 3");
        assertValidationSuccess(() -> sizeValidator.validate(Arrays.asList("1", "2", "3")));
        SizeValidator sizeValidator2 = getValidator(SizeValidator.class, 2, 3);
        assertValidationErrorThrowableMessage(() -> sizeValidator2.validate(Arrays.asList("1")), "collection length 1 didn't between required min 2 and max 3");
    }

    @Test
    public void sizeValidatorMapTests() throws Exception {
        SizeValidator sizeValidator = getValidator(SizeValidator.class, -1, 3);
        assertValidationErrorThrowableMessage(() -> sizeValidator.validate(new HashMap(){{
            put("a", "a");
            put("b", "b");
            put("c", "c");
            put("d", "d");
            put("e", "e");
        }}), "map length 5 didn't between required min -1 and max 3");
        assertValidationSuccess(() -> sizeValidator.validate(new HashMap(){{
            put("a", "a");
            put("b", "b");
            put("c", "c");
        }}));
        SizeValidator sizeValidator2 = getValidator(SizeValidator.class, 2, 3);
        assertValidationErrorThrowableMessage(() -> sizeValidator2.validate(new HashMap(){{
            put("a", "a");
        }}), "map length 1 didn't between required min 2 and max 3");
    }

    @Test
    public void sizeValidatorArrayTests() throws Exception {
        SizeValidator sizeValidator = getValidator(SizeValidator.class, -1, 3);
        assertValidationErrorThrowableMessage(() -> sizeValidator.validate(new Object[]{"1", "2", "3", "4", "5"}), "array length 5 didn't between required min -1 and max 3");
        assertValidationSuccess(() -> sizeValidator.validate(new Object[]{"1", "2", "3"}));
        SizeValidator sizeValidator2 = getValidator(SizeValidator.class, 2, 3);
        assertValidationErrorThrowableMessage(() -> sizeValidator2.validate(new Object[]{"1"}), "array length 1 didn't between required min 2 and max 3");
    }

    @Test
    public void sizeValidatorNullTests() throws Exception {
        SizeValidator sizeValidator = getValidator(SizeValidator.class, -1, 3);
        assertValidationSuccess(() -> sizeValidator.validate(null));
    }

    @Test
    public void notNullValidatorNullTests() throws Exception {
        NotNullValidator notNullValidator = getValidator(NotNullValidator.class);
        assertValidationSuccess(() -> notNullValidator.validate(""));
        assertValidationErrorThrowableMessage(() -> notNullValidator.validate(null), "value is null");
    }

    @Test
    public void requiredValidatorNullObjectTests() throws Exception {
        RequiredValidator requiredValidator = getValidator(RequiredValidator.class);
        assertValidationSuccess(() -> requiredValidator.validate("not empty"));
        assertValidationErrorThrowableMessage(() -> requiredValidator.validate(null), "value is null");
    }

    @Test
    public void nullValidatorNullTests() throws Exception {
        NullValidator notNullValidator = getValidator(NullValidator.class);
        assertValidationSuccess(() -> notNullValidator.validate(null));
        assertValidationErrorThrowableMessage(() -> notNullValidator.validate(""), "value is not null");
    }

    @Test
    public void notEmptyValidatorNullTests() throws Exception {
        NotEmptyValidator notEmptyValidator = getValidator(NotEmptyValidator.class);
        assertValidationErrorThrowableMessage(() -> notEmptyValidator.validate(""), "string is empty");
        assertValidationErrorThrowableMessage(() -> notEmptyValidator.validate(new ArrayList<>()), "collection is empty");
        assertValidationErrorThrowableMessage(() -> notEmptyValidator.validate(new HashMap<>()), "map is empty");
        assertValidationErrorThrowableMessage(() -> notEmptyValidator.validate(new String[0]), "array is empty");
        assertValidationSuccess(() -> notEmptyValidator.validate(null));
        assertValidationSuccess(() -> notEmptyValidator.validate("a"));
        assertValidationSuccess(() -> notEmptyValidator.validate(Arrays.asList("a")));
        assertValidationSuccess(() -> notEmptyValidator.validate(new String[]{"a"}));
        assertValidationSuccess(() -> notEmptyValidator.validate(new HashMap(){{
            put("a", "a");
        }}));
    }

    @Test
    public void requiredValidatorNullTests() throws Exception {
        RequiredValidator requiredValidator = getValidator(RequiredValidator.class);
        assertValidationErrorThrowableMessage(() -> requiredValidator.validate(""), "string is empty");
        assertValidationErrorThrowableMessage(() -> requiredValidator.validate(new ArrayList<>()), "collection is empty");
        assertValidationErrorThrowableMessage(() -> requiredValidator.validate(new HashMap<>()), "map is empty");
        assertValidationErrorThrowableMessage(() -> requiredValidator.validate(new String[0]), "array is empty");
        assertValidationSuccess(() -> requiredValidator.validate("a"));
        assertValidationSuccess(() -> requiredValidator.validate(Arrays.asList("a")));
        assertValidationSuccess(() -> requiredValidator.validate(new String[]{"a"}));
        assertValidationSuccess(() -> requiredValidator.validate(new HashMap(){{
            put("a", "a");
        }}));
    }

    @Test
    public void regexTest() throws Exception {
        RegexValidator regexValidator = getValidator(RegexValidator.class, "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", 0);
        assertValidationErrorThrowableMessage(() -> regexValidator.validate("no match"), "string no match does not match pattern ^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        assertValidationSuccess(() -> regexValidator.validate("https://www.google.com/someurl.html"));
        assertValidationErrorThrowableMessage(() -> regexValidator.validate(12312312), "value 12312312 is not a string");
        assertValidationSuccess(() -> regexValidator.validate(null));
    }

    @Test
    public void maxValidatorTests() throws Exception {
        MaxValidator maxValidator = getValidator(MaxValidator.class, 25);
        assertValidationErrorThrowableMessage(() -> maxValidator.validate(26), "value 26 is more than 25");
        assertValidationErrorThrowableMessage(() -> maxValidator.validate(26L), "value 26 is more than 25");
        assertValidationErrorThrowableMessage(() -> maxValidator.validate(new BigDecimal(26L)), "value 26 is more than 25");
        assertValidationErrorThrowableMessage(() -> maxValidator.validate(BigInteger.valueOf(26L)), "value 26 is more than 25");
        assertValidationErrorThrowableMessage(() -> maxValidator.validate(new Byte((byte) 26)), "value 26 is more than 25");
        assertValidationErrorThrowableMessage(() -> maxValidator.validate(new Short((byte) 26)), "value 26 is more than 25");
        assertValidationSuccess(() -> maxValidator.validate(""));
        assertValidationSuccess(() -> maxValidator.validate(null));
        assertValidationSuccess(() -> maxValidator.validate(24));
        assertValidationSuccess(() -> maxValidator.validate(24L));
        assertValidationSuccess(() -> maxValidator.validate(new BigDecimal(24L)));
        assertValidationSuccess(() -> maxValidator.validate(BigInteger.valueOf(24L)));
        assertValidationSuccess(() -> maxValidator.validate(new Byte((byte) 24)));
        assertValidationSuccess(() -> maxValidator.validate(new Short((byte) 24)));
    }

    @Test
    public void minValidatorTests() throws Exception {
        MinValidator minValidator = getValidator(MinValidator.class, 25);
        assertValidationErrorThrowableMessage(() -> minValidator.validate(24), "value 24 is less than 25");
        assertValidationErrorThrowableMessage(() -> minValidator.validate(24L), "value 24 is less than 25");
        assertValidationErrorThrowableMessage(() -> minValidator.validate(new BigDecimal(24L)), "value 24 is less than 25");
        assertValidationErrorThrowableMessage(() -> minValidator.validate(BigInteger.valueOf(24L)), "value 24 is less than 25");
        assertValidationErrorThrowableMessage(() -> minValidator.validate(new Byte((byte) 24)), "value 24 is less than 25");
        assertValidationErrorThrowableMessage(() -> minValidator.validate(new Short((byte) 24)), "value 24 is less than 25");
        assertValidationSuccess(() -> minValidator.validate(""));
        assertValidationSuccess(() -> minValidator.validate(null));
        assertValidationSuccess(() -> minValidator.validate(26));
        assertValidationSuccess(() -> minValidator.validate(26L));
        assertValidationSuccess(() -> minValidator.validate(new BigDecimal(26L)));
        assertValidationSuccess(() -> minValidator.validate(BigInteger.valueOf(26L)));
        assertValidationSuccess(() -> minValidator.validate(new Byte((byte) 26)));
        assertValidationSuccess(() -> minValidator.validate(new Short((byte) 26)));
    }

    @Test
    public void decimalMaxValidatorTests() throws Exception {
        DecimalMaxValidator max = getValidator(DecimalMaxValidator.class, "25.99342532525");
        assertValidationErrorThrowableMessage(() -> max.validate(26), "value 26 is more than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> max.validate(26L), "value 26 is more than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> max.validate(new BigDecimal(26L)), "value 26 is more than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> max.validate(BigInteger.valueOf(26L)), "value 26 is more than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> max.validate(new Byte((byte) 26)), "value 26 is more than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> max.validate(new Short((byte) 26)), "value 26 is more than 25.99342532525");
        assertValidationSuccess(() -> max.validate(""));
        assertValidationSuccess(() -> max.validate(null));
        assertValidationSuccess(() -> max.validate(24));
        assertValidationSuccess(() -> max.validate(24L));
        assertValidationSuccess(() -> max.validate(new BigDecimal(24L)));
        assertValidationSuccess(() -> max.validate(BigInteger.valueOf(24L)));
        assertValidationSuccess(() -> max.validate(new Byte((byte) 24)));
        assertValidationSuccess(() -> max.validate(new Short((byte) 24)));
    }

    @Test
    public void decimalMinValidatorTests() throws Exception {
        DecimalMinValidator min = getValidator(DecimalMinValidator.class, "25.99342532525");
        assertValidationErrorThrowableMessage(() -> min.validate(24), "value 24 is less than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> min.validate(24L), "value 24 is less than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> min.validate(new BigDecimal(24L)), "value 24 is less than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> min.validate(BigInteger.valueOf(24L)), "value 24 is less than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> min.validate(new Byte((byte) 24)), "value 24 is less than 25.99342532525");
        assertValidationErrorThrowableMessage(() -> min.validate(new Short((byte) 24)), "value 24 is less than 25.99342532525");
        assertValidationSuccess(() -> min.validate(""));
        assertValidationSuccess(() -> min.validate(null));
        assertValidationSuccess(() -> min.validate(26));
        assertValidationSuccess(() -> min.validate(26L));
        assertValidationSuccess(() -> min.validate(new BigDecimal(26L)));
        assertValidationSuccess(() -> min.validate(BigInteger.valueOf(26L)));
        assertValidationSuccess(() -> min.validate(new Byte((byte) 26)));
        assertValidationSuccess(() -> min.validate(new Short((byte) 26)));
    }

    @Test
    public void booleanValidatorNullTests() throws Exception {
        BoolValidator boolValidator = getValidator(BoolValidator.class, true);
        assertValidationSuccess(() -> boolValidator.validate(null));
        assertValidationSuccess(() -> boolValidator.validate(true));
        assertValidationSuccess(() -> boolValidator.validate(java.lang.Boolean.TRUE));
        assertValidationSuccess(() -> boolValidator.validate(new ArrayList<>()));
        assertValidationErrorThrowableMessage(() -> boolValidator.validate(java.lang.Boolean.FALSE), "value false doesn't match the boolean true");
        assertValidationErrorThrowableMessage(() -> boolValidator.validate(false), "value false doesn't match the boolean true");
    }


    @Test
    public void afterValidatorTest() throws Exception {
        withNormalizedTimezone(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
            AfterValidator afterValidator = getValidator(AfterValidator.class, "E, d MMM yyyy HH:mm:ss z", "Mon, 1 Apr 2019 11:05:30 GMT");
            assertValidationSuccess(
                    () -> afterValidator.validate(ZonedDateTime.parse("2019-05-27 10:15:30 AM +05:30", formatter)));
            assertValidationErrorThrowableMessage(() -> afterValidator.validate(
                    ZonedDateTime.parse("2018-02-27 10:15:30 AM +05:30", formatter)),
                    "2018-02-27T10:15:30+05:30 isn't after 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(
                    () -> afterValidator.validate(LocalDateTime.parse("2019-05-27 10:15:30 AM +05:30", formatter)));
            assertValidationErrorThrowableMessage(() -> afterValidator.validate(
                    LocalDateTime.parse("2018-02-27 10:15:30 AM +05:30", formatter)),
                    "2018-02-27T10:15:30 isn't after 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(() -> afterValidator.validate(simpleDateFormat.parse("2019-05-27")));
            assertValidationErrorThrowableMessage(() -> afterValidator.validate(
                    simpleDateFormat.parse("2018-02-27")),
                    "Tue Feb 27 00:00:00 CET 2018 isn't after 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(() -> afterValidator.validate(toCalendar(simpleDateFormat.parse("2019-05-27"))));
            assertValidationErrorThrowableMessage(() -> afterValidator.validate(
                    toCalendar(simpleDateFormat.parse("2018-02-27"))),
                    "Tue Feb 27 00:00:00 CET 2018 isn't after 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(
                    () -> afterValidator.validate(new Timestamp(simpleDateFormat.parse("2019-05-27").getTime())));
            assertValidationErrorThrowableMessage(() -> afterValidator.validate(
                    new Timestamp(simpleDateFormat.parse("2018-02-27").getTime())),
                    "2018-02-27 00:00:00.0 isn't after 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(() -> afterValidator.validate(LocalDate.parse("2019-05-27")));
            assertValidationErrorThrowableMessage(() -> afterValidator.validate(
                    LocalDate.parse("2018-02-27")),
                    "2018-02-27 isn't after 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(() -> afterValidator.validate(simpleDateFormat.parse("2019-05-27").getTime()));
            assertValidationErrorThrowableMessage(() -> afterValidator.validate(
                    simpleDateFormat.parse("2018-02-27").getTime()),
                    "1519686000000 isn't after 2019-04-01T11:05:30Z[GMT]");
            return null;
        });
    }


    @Test
    public void beforeValidatorTest() throws Exception {
        withNormalizedTimezone(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
            BeforeValidator beforeValidator = getValidator(BeforeValidator.class, "E, d MMM yyyy HH:mm:ss z", "Mon, 1 Apr 2019 11:05:30 GMT");
            assertValidationSuccess(
                    () -> beforeValidator.validate(ZonedDateTime.parse("2019-03-27 10:15:30 AM +05:30", formatter)));
            assertValidationErrorThrowableMessage(() -> beforeValidator.validate(
                    ZonedDateTime.parse("2019-04-02 10:15:30 AM +05:30", formatter)),
                    "2019-04-02T10:15:30+05:30 isn't before 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(
                    () -> beforeValidator.validate(LocalDateTime.parse("2019-03-27 10:15:30 AM +05:30", formatter)));
            assertValidationErrorThrowableMessage(() -> beforeValidator.validate(
                    LocalDateTime.parse("2019-04-27 10:15:30 AM +05:30", formatter)),
                    "2019-04-27T10:15:30 isn't before 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(() -> beforeValidator.validate(simpleDateFormat.parse("2019-02-27")));
            assertValidationErrorThrowableMessage(() -> beforeValidator.validate(
                    simpleDateFormat.parse("2019-05-27")),
                    "Mon May 27 00:00:00 CEST 2019 isn't before 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(() -> beforeValidator.validate(toCalendar(simpleDateFormat.parse("2019-02-27"))));
            assertValidationErrorThrowableMessage(() -> beforeValidator.validate(
                    toCalendar(simpleDateFormat.parse("2019-05-27"))),
                    "Mon May 27 00:00:00 CEST 2019 isn't before 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(
                    () -> beforeValidator.validate(new Timestamp(simpleDateFormat.parse("2019-03-27").getTime())));
            assertValidationErrorThrowableMessage(() -> beforeValidator.validate(
                    new Timestamp(simpleDateFormat.parse("2019-05-27").getTime())),
                    "2019-05-27 00:00:00.0 isn't before 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(() -> beforeValidator.validate(LocalDate.parse("2019-02-27")));
            assertValidationErrorThrowableMessage(() -> beforeValidator.validate(
                    LocalDate.parse("2019-05-27")),
                    "2019-05-27 isn't before 2019-04-01T11:05:30Z[GMT]");
            assertValidationSuccess(() -> beforeValidator.validate(simpleDateFormat.parse("2019-03-27").getTime()));
            assertValidationErrorThrowableMessage(() -> beforeValidator.validate(
                    simpleDateFormat.parse("2019-05-27").getTime()),
                    "1558908000000 isn't before 2019-04-01T11:05:30Z[GMT]");
            return null;
        });
    }

    private void withNormalizedTimezone(final Callable<Void> test) throws Exception {
        TimeZone defaultTimeZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
            test.call();
        } finally {
            TimeZone.setDefault(defaultTimeZone);
        }

    }

    @Test
    public void pastValidatorTest() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextYear = now.plusYears(1);
        ZonedDateTime lastYear = now.minusYears(1);
        withNormalizedTimezone(() -> {
           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
           PastValidator pastValidator = getValidator(PastValidator.class);
           assertValidationSuccess(
                   () -> pastValidator.validate(ZonedDateTime.parse(lastYear.getYear() + "-03-27 10:15:30 AM +05:30", formatter)));
           assertValidationErrorContainsThrowableMessage(() -> pastValidator.validate(
                   ZonedDateTime.parse(nextYear.getYear() + "-04-02 10:15:30 AM +05:30", formatter)),
                   "isn't before");
           assertValidationSuccess(
                   () -> pastValidator.validate(LocalDateTime.parse(lastYear.getYear() + "-03-27 10:15:30 AM +05:30", formatter)));
           assertValidationErrorContainsThrowableMessage(() -> pastValidator.validate(
                   LocalDateTime.parse(nextYear.getYear() + "-04-27 10:15:30 AM +05:30", formatter)),
                   "isn't before");
           assertValidationSuccess(() -> pastValidator.validate(simpleDateFormat.parse(lastYear.getYear() + "-02-27")));
           assertValidationErrorContainsThrowableMessage(() -> pastValidator.validate(
                   simpleDateFormat.parse(nextYear.getYear() + "-05-27")),
                   "isn't before");
           assertValidationSuccess(() -> pastValidator.validate(toCalendar(simpleDateFormat.parse(lastYear.getYear() + "-02-27"))));
           assertValidationErrorContainsThrowableMessage(() -> pastValidator.validate(
                   toCalendar(simpleDateFormat.parse(nextYear.getYear() + "-05-27"))),
                   "isn't before");
           assertValidationSuccess(
                   () -> pastValidator.validate(new Timestamp(simpleDateFormat.parse(lastYear.getYear() + "-03-27").getTime())));
           assertValidationErrorContainsThrowableMessage(() -> pastValidator.validate(
                   new Timestamp(simpleDateFormat.parse(nextYear.getYear() + "-05-27").getTime())),
                   "isn't before");
           assertValidationSuccess(() -> pastValidator.validate(LocalDate.parse(lastYear.getYear() + "-02-27")));
           assertValidationErrorContainsThrowableMessage(() -> pastValidator.validate(
                   LocalDate.parse(nextYear.getYear() + "-05-27")),
                   "isn't before");
           assertValidationSuccess(() -> pastValidator.validate(simpleDateFormat.parse(lastYear.getYear() + "-03-27").getTime()));
           assertValidationErrorContainsThrowableMessage(() -> pastValidator.validate(
                   simpleDateFormat.parse(nextYear.getYear() + "-05-27").getTime()),
                   "isn't before");
           return  null;
       });
    }

    @Test
    public void futureValidatorTest() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextYear = now.plusYears(1);
        ZonedDateTime lastYear = now.minusYears(1);
        withNormalizedTimezone(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
            FutureValidator futureValidator = getValidator(FutureValidator.class);
            assertValidationSuccess(
                    () -> futureValidator.validate(ZonedDateTime.parse(nextYear.getYear() + "-05-27 10:15:30 AM +05:30", formatter)));
            assertValidationErrorContainsThrowableMessage(() -> futureValidator.validate(
                    ZonedDateTime.parse(lastYear.getYear() + "-02-27 10:15:30 AM +05:30", formatter)),
                    "isn't after");
            assertValidationSuccess(
                    () -> futureValidator.validate(LocalDateTime.parse(nextYear.getYear() + "-05-27 10:15:30 AM +05:30", formatter)));
            assertValidationErrorContainsThrowableMessage(() -> futureValidator.validate(
                    LocalDateTime.parse(lastYear.getYear() + "-02-27 10:15:30 AM +05:30", formatter)),
                    "isn't after");
            assertValidationSuccess(() -> futureValidator.validate(simpleDateFormat.parse(nextYear.getYear() + "-05-27")));
            assertValidationErrorContainsThrowableMessage(() -> futureValidator.validate(
                    simpleDateFormat.parse(lastYear.getYear() + "-02-27")),
                    "isn't after");
            assertValidationSuccess(() -> futureValidator.validate(toCalendar(simpleDateFormat.parse(nextYear.getYear() + "-05-27"))));
            assertValidationErrorContainsThrowableMessage(() -> futureValidator.validate(
                    toCalendar(simpleDateFormat.parse(lastYear.getYear() + "-02-27"))),
                    "isn't after");
            assertValidationSuccess(
                    () -> futureValidator.validate(new Timestamp(simpleDateFormat.parse(nextYear.getYear() + "-05-27").getTime())));
            assertValidationErrorContainsThrowableMessage(() -> futureValidator.validate(
                    new Timestamp(simpleDateFormat.parse(lastYear.getYear() + "-02-27").getTime())),
                    "isn't after");
            assertValidationSuccess(() -> futureValidator.validate(LocalDate.parse(nextYear.getYear() + "-05-27")));
            assertValidationErrorContainsThrowableMessage(() -> futureValidator.validate(
                    LocalDate.parse(lastYear.getYear() + "-02-27")),
                    "isn't after");
            assertValidationSuccess(() -> futureValidator.validate(simpleDateFormat.parse(nextYear.getYear() + "-05-27").getTime()));
            assertValidationErrorContainsThrowableMessage(() -> futureValidator.validate(
                    simpleDateFormat.parse(lastYear.getYear() + "-02-27").getTime()),
                    "isn't after");
            return null;
        });
    }


    private void assertValidationSuccess(Callable<ValidationError> validationErrorCallable) throws Exception {
        ValidationError validationError = validationErrorCallable.call();
        assertNull(validationError);
    }

    private void assertValidationErrorThrowableMessage(Callable<ValidationError> validationErrorCallable, String detailErrorMessage)
            throws Exception {
        ValidationError validationError = validationErrorCallable.call();
        assertEquals(detailErrorMessage, validationError.getThrowable().getMessage());
    }

    private void assertValidationErrorContainsThrowableMessage(Callable<ValidationError> validationErrorCallable, String detailErrorMessage)
            throws Exception {
        ValidationError validationError = validationErrorCallable.call();
        String message = validationError.getThrowable().getMessage();
        assertTrue(message, message.contains(detailErrorMessage));
    }

    private <T extends Validator> T getValidator(Class<? extends Validator> validatorClazz, Object... args)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Constructor<?>> list = Arrays.stream(validatorClazz.getConstructors()).filter(
                constructor -> constructor.getParameterCount() == args.length).collect(Collectors.toList());
        assertEquals(1, list.size());
        return (T) list.get(0).newInstance(args);
    }

}

