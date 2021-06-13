package com.github.vincentrussell.validation.util;

import com.github.vincentrussell.validation.ValidationError;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public final class ValidationUtils {

    private ValidationUtils() {

    }

    /**
     * validation test to make sure that the passed in statement is true.
     *
     * @param statement                   validate this statement for truth.
     * @param stringFormatterErrorMessage error message to use for {@link java.lang.IllegalArgumentException}.
     * @param args                        the args to sue for the String formatter
     * @return the {@link com.github.vincentrussell.validation.ValidationError} if there is an error or null
     * if there isn't an error.
     */
    public static ValidationError isTrue(final boolean statement,
                                         final String stringFormatterErrorMessage,
                                         final Object... args) {
        if (!statement) {
            return new ValidationError(new IllegalArgumentException(String.format(stringFormatterErrorMessage, args)));
        }
        return null;
    }

    /**
     * Null safe comparison for {@link Number}s as {@link BigDecimal}.
     * @param n1 the first number
     * @param n2 the second number
     * @param <T> a type that is a {@link Number}
     * @return a negative integer, zero, or a positive integer as the first object is less than,
     * equal to, or greater than the second object.
     */
    public static <T extends Number> int bigDecimalCompareTo(final T n1, final T n2) {
        return bigDecimalCompareTo(convertToBigDecimal(n1), //
                convertToBigDecimal(n2));
    }

    private static <T extends Number> BigDecimal convertToBigDecimal(final T number) {
        if (BigDecimal.class.isInstance(number)) {
            return (BigDecimal) number;
        }
        return number != null ? new BigDecimal(number.toString()) : null;
    }

    /**
     * Null safe comparison for {@link BigDecimal}s.
     * @param n1 the first number
     * @param n2 the second number
     * @param <T> a type that is a {@link Number}
     * @return a negative integer, zero, or a positive integer as the first object is less than,
     * equal to, or greater than the second object.
     */
    public static <T extends Number> int bigDecimalCompareTo(final BigDecimal n1, final BigDecimal n2) {
        return compareTo(n1, n2);
    }

    /**
     *  Null safe comparison for {@link Comparable}s. <br>
     * <b>Be careful when you place this method inside {@link Comparable#compareTo(Object)} as such a
     *  method call will loop.</b>
     * @param c1 the first number to compare
     * @param c2 the second number to compare
     * @return a negative integer, zero, or a positive integer as the first object is less than,
     * equal to, or greater than the second object.
     * @param <T> the type number that need to be compared
     */
    public static <T extends Comparable<T>> int compareTo(final T c1, final T c2) {
        final boolean firstArgumentIsNull = c1 == null;
        final boolean secondArgumentIsNull = c2 == null;
        return firstArgumentIsNull ^ secondArgumentIsNull
                ? firstArgumentIsNull ? -1 : 1 : firstArgumentIsNull
                && secondArgumentIsNull ? 0 : c1.compareTo(c2);
    }

    /**
     * validation test to make sure that the first date is after the second date.
     * @param object the first date
     * @param dateTime the second date
     * @return the {@link ValidationError} or null if there isn't an error.
     */
    public static ValidationError isDateAfter(final Object object, final ZonedDateTime dateTime) {
        if (ChronoZonedDateTime.class.isInstance(object)) {
            return ValidationUtils.isTrue(((ChronoZonedDateTime) object).isAfter(dateTime),
                    "%s isn't after %s", object, dateTime);
        } else  if (ChronoLocalDateTime.class.isInstance(object)) {
            return ValidationUtils.isTrue(((ChronoLocalDateTime) object).isAfter(dateTime.toLocalDateTime()),
                    "%s isn't after %s", object, dateTime);
        } else if (Date.class.isInstance(object)) {
            return ValidationUtils.isTrue(((Date) object).toInstant().isAfter(dateTime.toInstant()),
                    "%s isn't after %s", object, dateTime);
        } else if (Calendar.class.isInstance(object)) {
            return ValidationUtils.isTrue(((Calendar) object).toInstant().isAfter(dateTime.toInstant()),
                    "%s isn't after %s", ((Calendar) object).getTime(), dateTime);
        } else if (Timestamp.class.isInstance(object)) {
            return ValidationUtils.isTrue(((Timestamp) object).toInstant().isAfter(dateTime.toInstant()),
                    "%s isn't after %s", object, dateTime);
        } else if (LocalDate.class.isInstance(object)) {
            return ValidationUtils.isTrue(((LocalDate) object).isAfter(dateTime.toLocalDateTime().toLocalDate()),
                    "%s isn't after %s", object, dateTime);
        } else if (Long.class.isInstance(object)) {
            return ValidationUtils.isTrue(new Date((long) object).toInstant().isAfter(dateTime.toInstant()),
                    "%s isn't after %s", object, dateTime);
        }
        return null;
    }


    /**
     * validation test to make sure that the first date is before the second date.
     * @param object the first date
     * @param dateTime the second date
     * @return the {@link ValidationError} or null if there isn't an error.
     */
    public static ValidationError isDateBefore(final Object object, final ZonedDateTime dateTime) {
        if (ChronoZonedDateTime.class.isInstance(object)) {
            return ValidationUtils.isTrue(((ChronoZonedDateTime) object).isBefore(dateTime),
                    "%s isn't before %s", object, dateTime);
        } else  if (ChronoLocalDateTime.class.isInstance(object)) {
            return ValidationUtils.isTrue(((ChronoLocalDateTime) object).isBefore(dateTime.toLocalDateTime()),
                    "%s isn't before %s", object, dateTime);
        } else if (Date.class.isInstance(object)) {
            return ValidationUtils.isTrue(((Date) object).toInstant().isBefore(dateTime.toInstant()),
                    "%s isn't before %s", object, dateTime);
        } else if (Calendar.class.isInstance(object)) {
            return ValidationUtils.isTrue(((Calendar) object).toInstant().isBefore(dateTime.toInstant()),
                    "%s isn't before %s", ((Calendar) object).getTime(), dateTime);
        } else if (Timestamp.class.isInstance(object)) {
            return ValidationUtils.isTrue(((Timestamp) object).toInstant().isBefore(dateTime.toInstant()),
                    "%s isn't before %s", object, dateTime);
        } else if (LocalDate.class.isInstance(object)) {
            return ValidationUtils.isTrue(((LocalDate) object).isBefore(dateTime.toLocalDateTime().toLocalDate()),
                    "%s isn't before %s", object, dateTime);
        } else if (Long.class.isInstance(object)) {
            return ValidationUtils.isTrue(new Date((long) object).toInstant().isBefore(dateTime.toInstant()),
                    "%s isn't before %s", object, dateTime);
        }
        return null;
    }


}
