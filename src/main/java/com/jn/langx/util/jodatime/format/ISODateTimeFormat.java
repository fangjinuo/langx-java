/*
 *  Copyright 2001-2009 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.jn.langx.util.jodatime.format;

import com.jn.langx.util.jodatime.DateTimeFieldType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory that creates instances of DateTimeFormatter for the ISO8601 standard.
 * <p>
 * Datetime formatting is performed by the {@link com.jn.langx.util.jodatime.format.DateTimeFormatter} class.
 * Three classes provide factory methods to create formatters, and this is one.
 * The others are {@link com.jn.langx.util.jodatime.format.DateTimeFormat} and {@link DateTimeFormatterBuilder}.
 * <p>
 * ISO8601 is the international standard for data interchange. It defines a
 * framework, rather than an absolute standard. As a result this provider has a
 * number of methods that represent common uses of the framework. The most common
 * formats are {@link #date() date}, {@link #time() time}, and {@link #dateTime() dateTime}.
 * <p>
 * For example, to format a date time in ISO format:
 * <pre>
 * DateTime dt = new DateTime();
 * DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
 * String str = fmt.print(dt);
 * </pre>
 * <p>
 * It is important to understand that these formatters are not linked to
 * the <code>ISOChronology</code>. These formatters may be used with any
 * chronology, however there may be certain side effects with more unusual
 * chronologies. For example, the ISO formatters rely on dayOfWeek being
 * single digit, dayOfMonth being two digit and dayOfYear being three digit.
 * A chronology with a ten day week would thus cause issues. However, in
 * general, it is safe to use these formatters with other chronologies.
 * <p>
 * ISODateTimeFormat is thread-safe and immutable, and the formatters it
 * returns are as well.
 *
 * @author Brian S O'Neill
 * @see DateTimeFormat
 * @see DateTimeFormatterBuilder
 * @since 1.0
 */
public class ISODateTimeFormat {

    /**
     * Constructor.
     *
     * @since 1.1 (previously private)
     */
    protected ISODateTimeFormat() {
        super();
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a formatter that outputs only those fields specified.
     * <p>
     * This method examines the fields provided and returns an ISO-style
     * formatter that best fits. This can be useful for outputting
     * less-common ISO styles, such as YearMonth (YYYY-MM) or MonthDay (--MM-DD).
     * <p>
     * The list provided may have overlapping fields, such as dayOfWeek and
     * dayOfMonth. In this case, the style is chosen based on the following
     * list, thus in the example, the calendar style is chosen as dayOfMonth
     * is higher in priority than dayOfWeek:
     * <ul>
     * <li>monthOfYear - calendar date style
     * <li>dayOfYear - ordinal date style
     * <li>weekOfWeekYear - week date style
     * <li>dayOfMonth - calendar date style
     * <li>dayOfWeek - week date style
     * <li>year
     * <li>weekyear
     * </ul>
     * The supported formats are:
     * <pre>
     * Extended      Basic       Fields
     * 2005-03-25    20050325    year/monthOfYear/dayOfMonth
     * 2005-03       2005-03     year/monthOfYear
     * 2005--25      2005--25    year/dayOfMonth *
     * 2005          2005        year
     * --03-25       --0325      monthOfYear/dayOfMonth
     * --03          --03        monthOfYear
     * ---03         ---03       dayOfMonth
     * 2005-084      2005084     year/dayOfYear
     * -084          -084        dayOfYear
     * 2005-W12-5    2005W125    weekyear/weekOfWeekyear/dayOfWeek
     * 2005-W-5      2005W-5     weekyear/dayOfWeek *
     * 2005-W12      2005W12     weekyear/weekOfWeekyear
     * -W12-5        -W125       weekOfWeekyear/dayOfWeek
     * -W12          -W12        weekOfWeekyear
     * -W-5          -W-5        dayOfWeek
     * 10:20:30.040  102030.040  hour/minute/second/milli
     * 10:20:30      102030      hour/minute/second
     * 10:20         1020        hour/minute
     * 10            10          hour
     * -20:30.040    -2030.040   minute/second/milli
     * -20:30        -2030       minute/second
     * -20           -20         minute
     * --30.040      --30.040    second/milli
     * --30          --30        second
     * ---.040       ---.040     milli *
     * 10-30.040     10-30.040   hour/second/milli *
     * 10:20-.040    1020-.040   hour/minute/milli *
     * 10-30         10-30       hour/second *
     * 10--.040      10--.040    hour/milli *
     * -20-.040      -20-.040    minute/milli *
     *   plus datetime formats like {date}T{time}
     * </pre>
     * * indiates that this is not an official ISO format and can be excluded
     * by passing in <code>strictISO</code> as <code>true</code>.
     * <p>
     * This method can side effect the input collection of fields.
     * If the input collection is modifiable, then each field that was added to
     * the formatter will be removed from the collection, including any duplicates.
     * If the input collection is unmodifiable then no side effect occurs.
     * <p>
     * This side effect processing is useful if you need to know whether all
     * the fields were converted into the formatter or not. To achieve this,
     * pass in a modifiable list, and check that it is empty on exit.
     *
     * @param fields    the fields to get a formatter for, not null,
     *                  updated by the method call unless unmodifiable,
     *                  removing those fields built in the formatter
     * @param extended  true to use the extended format (with separators)
     * @param strictISO true to stick exactly to ISO8601, false to include additional formats
     * @return a suitable formatter
     * @throws IllegalArgumentException if there is no format for the fields
     * @since 1.1
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter forFields(
            Collection<DateTimeFieldType> fields,
            boolean extended,
            boolean strictISO) {

        if (fields == null || fields.size() == 0) {
            throw new IllegalArgumentException("The fields must not be null or empty");
        }
        Set<DateTimeFieldType> workingFields = new HashSet<DateTimeFieldType>(fields);
        int inputSize = workingFields.size();
        boolean reducedPrec = false;
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder();
        // date
        if (workingFields.contains(DateTimeFieldType.monthOfYear())) {
            reducedPrec = dateByMonth(bld, workingFields, extended, strictISO);
        } else if (workingFields.contains(DateTimeFieldType.dayOfYear())) {
            reducedPrec = dateByOrdinal(bld, workingFields, extended, strictISO);
        } else if (workingFields.contains(DateTimeFieldType.weekOfWeekyear())) {
            reducedPrec = dateByWeek(bld, workingFields, extended, strictISO);
        } else if (workingFields.contains(DateTimeFieldType.dayOfMonth())) {
            reducedPrec = dateByMonth(bld, workingFields, extended, strictISO);
        } else if (workingFields.contains(DateTimeFieldType.dayOfWeek())) {
            reducedPrec = dateByWeek(bld, workingFields, extended, strictISO);
        } else if (workingFields.remove(DateTimeFieldType.year())) {
            bld.append(Constants.ye);
            reducedPrec = true;
        } else if (workingFields.remove(DateTimeFieldType.weekyear())) {
            bld.append(Constants.we);
            reducedPrec = true;
        }
        boolean datePresent = (workingFields.size() < inputSize);

        // time
        time(bld, workingFields, extended, strictISO, reducedPrec, datePresent);

        // result
        if (bld.canBuildFormatter() == false) {
            throw new IllegalArgumentException("No valid format for fields: " + fields);
        }

        // side effect the input collection to indicate the processed fields
        // handling unmodifiable collections with no side effect
        try {
            fields.retainAll(workingFields);
        } catch (UnsupportedOperationException ex) {
            // ignore, so we can handle unmodifiable collections
        }
        return bld.toFormatter();
    }

    //-----------------------------------------------------------------------

    /**
     * Creates a date using the calendar date format.
     * Specification reference: 5.2.1.
     *
     * @param bld       the builder
     * @param fields    the fields
     * @param extended  true to use extended format
     * @param strictISO true to only allow ISO formats
     * @return true if reduced precision
     * @since 1.1
     */
    private static boolean dateByMonth(
            DateTimeFormatterBuilder bld,
            Collection<DateTimeFieldType> fields,
            boolean extended,
            boolean strictISO) {

        boolean reducedPrec = false;
        if (fields.remove(DateTimeFieldType.year())) {
            bld.append(Constants.ye);
            if (fields.remove(DateTimeFieldType.monthOfYear())) {
                if (fields.remove(DateTimeFieldType.dayOfMonth())) {
                    // YYYY-MM-DD/YYYYMMDD
                    appendSeparator(bld, extended);
                    bld.appendMonthOfYear(2);
                    appendSeparator(bld, extended);
                    bld.appendDayOfMonth(2);
                } else {
                    // YYYY-MM/YYYY-MM
                    bld.appendLiteral('-');
                    bld.appendMonthOfYear(2);
                    reducedPrec = true;
                }
            } else {
                if (fields.remove(DateTimeFieldType.dayOfMonth())) {
                    // YYYY--DD/YYYY--DD (non-iso)
                    checkNotStrictISO(fields, strictISO);
                    bld.appendLiteral('-');
                    bld.appendLiteral('-');
                    bld.appendDayOfMonth(2);
                } else {
                    // YYYY/YYYY
                    reducedPrec = true;
                }
            }

        } else if (fields.remove(DateTimeFieldType.monthOfYear())) {
            bld.appendLiteral('-');
            bld.appendLiteral('-');
            bld.appendMonthOfYear(2);
            if (fields.remove(DateTimeFieldType.dayOfMonth())) {
                // --MM-DD/--MMDD
                appendSeparator(bld, extended);
                bld.appendDayOfMonth(2);
            } else {
                // --MM/--MM
                reducedPrec = true;
            }
        } else if (fields.remove(DateTimeFieldType.dayOfMonth())) {
            // ---DD/---DD
            bld.appendLiteral('-');
            bld.appendLiteral('-');
            bld.appendLiteral('-');
            bld.appendDayOfMonth(2);
        }
        return reducedPrec;
    }

    //-----------------------------------------------------------------------

    /**
     * Creates a date using the ordinal date format.
     * Specification reference: 5.2.2.
     *
     * @param bld       the builder
     * @param fields    the fields
     * @param extended  true to use extended format
     * @param strictISO true to only allow ISO formats
     * @since 1.1
     */
    private static boolean dateByOrdinal(
            DateTimeFormatterBuilder bld,
            Collection<DateTimeFieldType> fields,
            boolean extended,
            boolean strictISO) {

        boolean reducedPrec = false;
        if (fields.remove(DateTimeFieldType.year())) {
            bld.append(Constants.ye);
            if (fields.remove(DateTimeFieldType.dayOfYear())) {
                // YYYY-DDD/YYYYDDD
                appendSeparator(bld, extended);
                bld.appendDayOfYear(3);
            } else {
                // YYYY/YYYY
                reducedPrec = true;
            }

        } else if (fields.remove(DateTimeFieldType.dayOfYear())) {
            // -DDD/-DDD
            bld.appendLiteral('-');
            bld.appendDayOfYear(3);
        }
        return reducedPrec;
    }

    //-----------------------------------------------------------------------

    /**
     * Creates a date using the calendar date format.
     * Specification reference: 5.2.3.
     *
     * @param bld       the builder
     * @param fields    the fields
     * @param extended  true to use extended format
     * @param strictISO true to only allow ISO formats
     * @since 1.1
     */
    private static boolean dateByWeek(
            DateTimeFormatterBuilder bld,
            Collection<DateTimeFieldType> fields,
            boolean extended,
            boolean strictISO) {

        boolean reducedPrec = false;
        if (fields.remove(DateTimeFieldType.weekyear())) {
            bld.append(Constants.we);
            if (fields.remove(DateTimeFieldType.weekOfWeekyear())) {
                appendSeparator(bld, extended);
                bld.appendLiteral('W');
                bld.appendWeekOfWeekyear(2);
                if (fields.remove(DateTimeFieldType.dayOfWeek())) {
                    // YYYY-WWW-D/YYYYWWWD
                    appendSeparator(bld, extended);
                    bld.appendDayOfWeek(1);
                } else {
                    // YYYY-WWW/YYYY-WWW
                    reducedPrec = true;
                }
            } else {
                if (fields.remove(DateTimeFieldType.dayOfWeek())) {
                    // YYYY-W-D/YYYYW-D (non-iso)
                    checkNotStrictISO(fields, strictISO);
                    appendSeparator(bld, extended);
                    bld.appendLiteral('W');
                    bld.appendLiteral('-');
                    bld.appendDayOfWeek(1);
                } else {
                    // YYYY/YYYY
                    reducedPrec = true;
                }
            }

        } else if (fields.remove(DateTimeFieldType.weekOfWeekyear())) {
            bld.appendLiteral('-');
            bld.appendLiteral('W');
            bld.appendWeekOfWeekyear(2);
            if (fields.remove(DateTimeFieldType.dayOfWeek())) {
                // -WWW-D/-WWWD
                appendSeparator(bld, extended);
                bld.appendDayOfWeek(1);
            } else {
                // -WWW/-WWW
                reducedPrec = true;
            }
        } else if (fields.remove(DateTimeFieldType.dayOfWeek())) {
            // -W-D/-W-D
            bld.appendLiteral('-');
            bld.appendLiteral('W');
            bld.appendLiteral('-');
            bld.appendDayOfWeek(1);
        }
        return reducedPrec;
    }

    //-----------------------------------------------------------------------

    /**
     * Adds the time fields to the builder.
     * Specification reference: 5.3.1.
     *
     * @param bld         the builder
     * @param fields      the fields
     * @param extended    whether to use the extended format
     * @param strictISO   whether to be strict
     * @param reducedPrec whether the date was reduced precision
     * @param datePresent whether there was a date
     * @since 1.1
     */
    private static void time(
            DateTimeFormatterBuilder bld,
            Collection<DateTimeFieldType> fields,
            boolean extended,
            boolean strictISO,
            boolean reducedPrec,
            boolean datePresent) {

        boolean hour = fields.remove(DateTimeFieldType.hourOfDay());
        boolean minute = fields.remove(DateTimeFieldType.minuteOfHour());
        boolean second = fields.remove(DateTimeFieldType.secondOfMinute());
        boolean milli = fields.remove(DateTimeFieldType.millisOfSecond());
        if (!hour && !minute && !second && !milli) {
            return;
        }
        if (hour || minute || second || milli) {
            if (strictISO && reducedPrec) {
                throw new IllegalArgumentException("No valid ISO8601 format for fields because Date was reduced precision: " + fields);
            }
            if (datePresent) {
                bld.appendLiteral('T');
            }
        }
        if (hour && minute && second || (hour && !second && !milli)) {
            // OK - HMSm/HMS/HM/H - valid in combination with date
        } else {
            if (strictISO && datePresent) {
                throw new IllegalArgumentException("No valid ISO8601 format for fields because Time was truncated: " + fields);
            }
            if (!hour && (minute && second || (minute && !milli) || second)) {
                // OK - MSm/MS/M/Sm/S - valid ISO formats
            } else {
                if (strictISO) {
                    throw new IllegalArgumentException("No valid ISO8601 format for fields: " + fields);
                }
            }
        }
        if (hour) {
            bld.appendHourOfDay(2);
        } else if (minute || second || milli) {
            bld.appendLiteral('-');
        }
        if (extended && hour && minute) {
            bld.appendLiteral(':');
        }
        if (minute) {
            bld.appendMinuteOfHour(2);
        } else if (second || milli) {
            bld.appendLiteral('-');
        }
        if (extended && minute && second) {
            bld.appendLiteral(':');
        }
        if (second) {
            bld.appendSecondOfMinute(2);
        } else if (milli) {
            bld.appendLiteral('-');
        }
        if (milli) {
            bld.appendLiteral('.');
            bld.appendMillisOfSecond(3);
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Checks that the iso only flag is not set, throwing an exception if it is.
     *
     * @param fields    the fields
     * @param strictISO true if only ISO formats allowed
     * @since 1.1
     */
    private static void checkNotStrictISO(Collection<DateTimeFieldType> fields, boolean strictISO) {
        if (strictISO) {
            throw new IllegalArgumentException("No valid ISO8601 format for fields: " + fields);
        }
    }

    /**
     * Appends the separator if necessary.
     *
     * @param bld      the builder
     * @param extended whether to append the separator
     * @param sep      the separator
     * @since 1.1
     */
    private static void appendSeparator(DateTimeFormatterBuilder bld, boolean extended) {
        if (extended) {
            bld.appendLiteral('-');
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a generic ISO date parser for parsing dates with a possible zone.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * date              = date-element ['T' offset]
     * date-element      = std-date-element | ord-date-element | week-date-element
     * std-date-element  = yyyy ['-' MM ['-' dd]]
     * ord-date-element  = yyyy ['-' DDD]
     * week-date-element = xxxx '-W' ww ['-' e]
     * offset            = 'Z' | (('+' | '-') HH [':' mm [':' ss [('.' | ',') SSS]]])
     * </pre>
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateParser() {
        return Constants.dp;
    }

    /**
     * Returns a generic ISO date parser for parsing local dates.
     * This parser is initialised with the local (UTC) time zone.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * date-element      = std-date-element | ord-date-element | week-date-element
     * std-date-element  = yyyy ['-' MM ['-' dd]]
     * ord-date-element  = yyyy ['-' DDD]
     * week-date-element = xxxx '-W' ww ['-' e]
     * </pre>
     *
     * @since 1.3
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter localDateParser() {
        return Constants.ldp;
    }

    /**
     * Returns a generic ISO date parser for parsing dates.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * date-element      = std-date-element | ord-date-element | week-date-element
     * std-date-element  = yyyy ['-' MM ['-' dd]]
     * ord-date-element  = yyyy ['-' DDD]
     * week-date-element = xxxx '-W' ww ['-' e]
     * </pre>
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateElementParser() {
        return Constants.dpe;
    }

    /**
     * Returns a generic ISO time parser for parsing times with a possible zone.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * time           = ['T'] time-element [offset]
     * time-element   = HH [minute-element] | [fraction]
     * minute-element = ':' mm [second-element] | [fraction]
     * second-element = ':' ss [fraction]
     * fraction       = ('.' | ',') digit+
     * offset         = 'Z' | (('+' | '-') HH [':' mm [':' ss [('.' | ',') SSS]]])
     * </pre>
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter timeParser() {
        return Constants.tp;
    }

    /**
     * Returns a generic ISO time parser for parsing local times.
     * This parser is initialised with the local (UTC) time zone.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * time           = ['T'] time-element
     * time-element   = HH [minute-element] | [fraction]
     * minute-element = ':' mm [second-element] | [fraction]
     * second-element = ':' ss [fraction]
     * fraction       = ('.' | ',') digit+
     * </pre>
     *
     * @since 1.3
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter localTimeParser() {
        return Constants.ltp;
    }

    /**
     * Returns a generic ISO time parser.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * time-element   = HH [minute-element] | [fraction]
     * minute-element = ':' mm [second-element] | [fraction]
     * second-element = ':' ss [fraction]
     * fraction       = ('.' | ',') digit+
     * </pre>
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter timeElementParser() {
        return Constants.tpe;
    }

    /**
     * Returns a generic ISO datetime parser which parses either a date or
     * a time or both. The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * datetime          = time | date-opt-time
     * time              = 'T' time-element [offset]
     * date-opt-time     = date-element ['T' [time-element] [offset]]
     * date-element      = std-date-element | ord-date-element | week-date-element
     * std-date-element  = yyyy ['-' MM ['-' dd]]
     * ord-date-element  = yyyy ['-' DDD]
     * week-date-element = xxxx '-W' ww ['-' e]
     * time-element      = HH [minute-element] | [fraction]
     * minute-element    = ':' mm [second-element] | [fraction]
     * second-element    = ':' ss [fraction]
     * fraction          = ('.' | ',') digit+
     * offset            = 'Z' | (('+' | '-') HH [':' mm [':' ss [('.' | ',') SSS]]])
     * </pre>
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateTimeParser() {
        return Constants.dtp;
    }

    /**
     * Returns a generic ISO datetime parser where the date is mandatory and
     * the time is optional. This parser can parse zoned datetimes.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * date-opt-time     = date-element ['T' [time-element] [offset]]
     * date-element      = std-date-element | ord-date-element | week-date-element
     * std-date-element  = yyyy ['-' MM ['-' dd]]
     * ord-date-element  = yyyy ['-' DDD]
     * week-date-element = xxxx '-W' ww ['-' e]
     * time-element      = HH [minute-element] | [fraction]
     * minute-element    = ':' mm [second-element] | [fraction]
     * second-element    = ':' ss [fraction]
     * fraction          = ('.' | ',') digit+
     * </pre>
     *
     * @since 1.3
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateOptionalTimeParser() {
        return Constants.dotp;
    }

    /**
     * Returns a generic ISO datetime parser where the date is mandatory and
     * the time is optional. This parser only parses local datetimes.
     * This parser is initialised with the local (UTC) time zone.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     * <p>
     * It accepts formats described by the following syntax:
     * <pre>
     * datetime          = date-element ['T' time-element]
     * date-element      = std-date-element | ord-date-element | week-date-element
     * std-date-element  = yyyy ['-' MM ['-' dd]]
     * ord-date-element  = yyyy ['-' DDD]
     * week-date-element = xxxx '-W' ww ['-' e]
     * time-element      = HH [minute-element] | [fraction]
     * minute-element    = ':' mm [second-element] | [fraction]
     * second-element    = ':' ss [fraction]
     * fraction          = ('.' | ',') digit+
     * </pre>
     *
     * @since 1.3
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter localDateOptionalTimeParser() {
        return Constants.ldotp;
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a formatter for a full date as four digit year, two digit month
     * of year, and two digit day of month (yyyy-MM-dd).
     *
     * @return a formatter for yyyy-MM-dd
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter date() {
        return yearMonthDay();
    }

    /**
     * Returns a formatter for a two digit hour of day, two digit minute of
     * hour, two digit second of minute, three digit fraction of second, and
     * time zone offset (HH:mm:ss.SSSZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for HH:mm:ss.SSSZZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter time() {
        return Constants.t;
    }

    /**
     * Returns a formatter for a two digit hour of day, two digit minute of
     * hour, two digit second of minute, and time zone offset (HH:mm:ssZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for HH:mm:ssZZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter timeNoMillis() {
        return Constants.tx;
    }

    /**
     * Returns a formatter for a two digit hour of day, two digit minute of
     * hour, two digit second of minute, three digit fraction of second, and
     * time zone offset prefixed by 'T' ('T'HH:mm:ss.SSSZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for 'T'HH:mm:ss.SSSZZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter tTime() {
        return Constants.tt;
    }

    /**
     * Returns a formatter for a two digit hour of day, two digit minute of
     * hour, two digit second of minute, and time zone offset prefixed
     * by 'T' ('T'HH:mm:ssZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for 'T'HH:mm:ssZZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter tTimeNoMillis() {
        return Constants.ttx;
    }

    /**
     * Returns a formatter that combines a full date and time, separated by a 'T'
     * (yyyy-MM-dd'T'HH:mm:ss.SSSZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for yyyy-MM-dd'T'HH:mm:ss.SSSZZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateTime() {
        return Constants.dt;
    }

    /**
     * Returns a formatter that combines a full date and time without millis,
     * separated by a 'T' (yyyy-MM-dd'T'HH:mm:ssZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for yyyy-MM-dd'T'HH:mm:ssZZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateTimeNoMillis() {
        return Constants.dtx;
    }

    /**
     * Returns a formatter for a full ordinal date, using a four
     * digit year and three digit dayOfYear (yyyy-DDD).
     *
     * @return a formatter for yyyy-DDD
     * @since 1.1
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter ordinalDate() {
        return Constants.od;
    }

    /**
     * Returns a formatter for a full ordinal date and time, using a four
     * digit year and three digit dayOfYear (yyyy-DDD'T'HH:mm:ss.SSSZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for yyyy-DDD'T'HH:mm:ss.SSSZZ
     * @since 1.1
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter ordinalDateTime() {
        return Constants.odt;
    }

    /**
     * Returns a formatter for a full ordinal date and time without millis,
     * using a four digit year and three digit dayOfYear (yyyy-DDD'T'HH:mm:ssZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for yyyy-DDD'T'HH:mm:ssZZ
     * @since 1.1
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter ordinalDateTimeNoMillis() {
        return Constants.odtx;
    }

    /**
     * Returns a formatter for a full date as four digit weekyear, two digit
     * week of weekyear, and one digit day of week (xxxx-'W'ww-e).
     *
     * @return a formatter for xxxx-'W'ww-e
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter weekDate() {
        return Constants.wwd;
    }

    /**
     * Returns a formatter that combines a full weekyear date and time,
     * separated by a 'T' (xxxx-'W'ww-e'T'HH:mm:ss.SSSZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for xxxx-'W'ww-e'T'HH:mm:ss.SSSZZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter weekDateTime() {
        return Constants.wdt;
    }

    /**
     * Returns a formatter that combines a full weekyear date and time without millis,
     * separated by a 'T' (xxxx-'W'ww-e'T'HH:mm:ssZZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HH:mm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for xxxx-'W'ww-e'T'HH:mm:ssZZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter weekDateTimeNoMillis() {
        return Constants.wdtx;
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a basic formatter for a full date as four digit year, two digit
     * month of year, and two digit day of month (yyyyMMdd).
     *
     * @return a formatter for yyyyMMdd
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicDate() {
        return Constants.bd;
    }

    /**
     * Returns a basic formatter for a two digit hour of day, two digit minute
     * of hour, two digit second of minute, three digit millis, and time zone
     * offset (HHmmss.SSSZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for HHmmss.SSSZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicTime() {
        return Constants.bt;
    }

    /**
     * Returns a basic formatter for a two digit hour of day, two digit minute
     * of hour, two digit second of minute, and time zone offset (HHmmssZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for HHmmssZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicTimeNoMillis() {
        return Constants.btx;
    }

    /**
     * Returns a basic formatter for a two digit hour of day, two digit minute
     * of hour, two digit second of minute, three digit millis, and time zone
     * offset prefixed by 'T' ('T'HHmmss.SSSZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for 'T'HHmmss.SSSZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicTTime() {
        return Constants.btt;
    }

    /**
     * Returns a basic formatter for a two digit hour of day, two digit minute
     * of hour, two digit second of minute, and time zone offset prefixed by 'T'
     * ('T'HHmmssZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for 'T'HHmmssZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicTTimeNoMillis() {
        return Constants.bttx;
    }

    /**
     * Returns a basic formatter that combines a basic date and time, separated
     * by a 'T' (yyyyMMdd'T'HHmmss.SSSZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for yyyyMMdd'T'HHmmss.SSSZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicDateTime() {
        return Constants.bdt;
    }

    /**
     * Returns a basic formatter that combines a basic date and time without millis,
     * separated by a 'T' (yyyyMMdd'T'HHmmssZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for yyyyMMdd'T'HHmmssZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicDateTimeNoMillis() {
        return Constants.bdtx;
    }

    /**
     * Returns a formatter for a full ordinal date, using a four
     * digit year and three digit dayOfYear (yyyyDDD).
     *
     * @return a formatter for yyyyDDD
     * @since 1.1
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicOrdinalDate() {
        return Constants.bod;
    }

    /**
     * Returns a formatter for a full ordinal date and time, using a four
     * digit year and three digit dayOfYear (yyyyDDD'T'HHmmss.SSSZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for yyyyDDD'T'HHmmss.SSSZ
     * @since 1.1
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicOrdinalDateTime() {
        return Constants.bodt;
    }

    /**
     * Returns a formatter for a full ordinal date and time without millis,
     * using a four digit year and three digit dayOfYear (yyyyDDD'T'HHmmssZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for yyyyDDD'T'HHmmssZ
     * @since 1.1
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicOrdinalDateTimeNoMillis() {
        return Constants.bodtx;
    }

    /**
     * Returns a basic formatter for a full date as four digit weekyear, two
     * digit week of weekyear, and one digit day of week (xxxx'W'wwe).
     *
     * @return a formatter for xxxx'W'wwe
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicWeekDate() {
        return Constants.bwd;
    }

    /**
     * Returns a basic formatter that combines a basic weekyear date and time,
     * separated by a 'T' (xxxx'W'wwe'T'HHmmss.SSSZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for xxxx'W'wwe'T'HHmmss.SSSZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicWeekDateTime() {
        return Constants.bwdt;
    }

    /**
     * Returns a basic formatter that combines a basic weekyear date and time
     * without millis, separated by a 'T' (xxxx'W'wwe'T'HHmmssZ).
     * The time zone offset is 'Z' for zero, and of the form '\u00b1HHmm' for non-zero.
     * The parser is strict by default, thus time string {@code 24:00} cannot be parsed.
     *
     * @return a formatter for xxxx'W'wwe'T'HHmmssZ
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter basicWeekDateTimeNoMillis() {
        return Constants.bwdtx;
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a formatter for a four digit year. (yyyy)
     *
     * @return a formatter for yyyy
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter year() {
        return Constants.ye;
    }

    /**
     * Returns a formatter for a four digit year and two digit month of
     * year. (yyyy-MM)
     *
     * @return a formatter for yyyy-MM
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter yearMonth() {
        return Constants.ym;
    }

    /**
     * Returns a formatter for a four digit year, two digit month of year, and
     * two digit day of month. (yyyy-MM-dd)
     *
     * @return a formatter for yyyy-MM-dd
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter yearMonthDay() {
        return Constants.ymd;
    }

    /**
     * Returns a formatter for a four digit weekyear. (xxxx)
     *
     * @return a formatter for xxxx
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter weekyear() {
        return Constants.we;
    }

    /**
     * Returns a formatter for a four digit weekyear and two digit week of
     * weekyear. (xxxx-'W'ww)
     *
     * @return a formatter for xxxx-'W'ww
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter weekyearWeek() {
        return Constants.ww;
    }

    /**
     * Returns a formatter for a four digit weekyear, two digit week of
     * weekyear, and one digit day of week. (xxxx-'W'ww-e)
     *
     * @return a formatter for xxxx-'W'ww-e
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter weekyearWeekDay() {
        return Constants.wwd;
    }

    /**
     * Returns a formatter for a two digit hour of day. (HH)
     *
     * @return a formatter for HH
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter hour() {
        return Constants.hde;
    }

    /**
     * Returns a formatter for a two digit hour of day and two digit minute of
     * hour. (HH:mm)
     *
     * @return a formatter for HH:mm
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter hourMinute() {
        return Constants.hm;
    }

    /**
     * Returns a formatter for a two digit hour of day, two digit minute of
     * hour, and two digit second of minute. (HH:mm:ss)
     *
     * @return a formatter for HH:mm:ss
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter hourMinuteSecond() {
        return Constants.hms;
    }

    /**
     * Returns a formatter for a two digit hour of day, two digit minute of
     * hour, two digit second of minute, and three digit fraction of
     * second (HH:mm:ss.SSS). Parsing will parse up to 3 fractional second
     * digits.
     *
     * @return a formatter for HH:mm:ss.SSS
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter hourMinuteSecondMillis() {
        return Constants.hmsl;
    }

    /**
     * Returns a formatter for a two digit hour of day, two digit minute of
     * hour, two digit second of minute, and three digit fraction of
     * second (HH:mm:ss.SSS). Parsing will parse up to 9 fractional second
     * digits, throwing away all except the first three.
     *
     * @return a formatter for HH:mm:ss.SSS
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter hourMinuteSecondFraction() {
        return Constants.hmsf;
    }

    /**
     * Returns a formatter that combines a full date and two digit hour of
     * day. (yyyy-MM-dd'T'HH)
     *
     * @return a formatter for yyyy-MM-dd'T'HH
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHour() {
        return Constants.dh;
    }

    /**
     * Returns a formatter that combines a full date, two digit hour of day,
     * and two digit minute of hour. (yyyy-MM-dd'T'HH:mm)
     *
     * @return a formatter for yyyy-MM-dd'T'HH:mm
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHourMinute() {
        return Constants.dhm;
    }

    /**
     * Returns a formatter that combines a full date, two digit hour of day,
     * two digit minute of hour, and two digit second of
     * minute. (yyyy-MM-dd'T'HH:mm:ss)
     *
     * @return a formatter for yyyy-MM-dd'T'HH:mm:ss
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHourMinuteSecond() {
        return Constants.dhms;
    }

    /**
     * Returns a formatter that combines a full date, two digit hour of day,
     * two digit minute of hour, two digit second of minute, and three digit
     * fraction of second (yyyy-MM-dd'T'HH:mm:ss.SSS). Parsing will parse up
     * to 3 fractional second digits.
     *
     * @return a formatter for yyyy-MM-dd'T'HH:mm:ss.SSS
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHourMinuteSecondMillis() {
        return Constants.dhmsl;
    }

    /**
     * Returns a formatter that combines a full date, two digit hour of day,
     * two digit minute of hour, two digit second of minute, and three digit
     * fraction of second (yyyy-MM-dd'T'HH:mm:ss.SSS). Parsing will parse up
     * to 9 fractional second digits, throwing away all except the first three.
     *
     * @return a formatter for yyyy-MM-dd'T'HH:mm:ss.SSS
     */
    public static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHourMinuteSecondFraction() {
        return Constants.dhmsf;
    }

    //-----------------------------------------------------------------------
    static final class Constants {
        private static final com.jn.langx.util.jodatime.format.DateTimeFormatter
                ye = yearElement(),  // year element (yyyy)
                mye = monthElement(), // monthOfYear element (-MM)
                dme = dayOfMonthElement(), // dayOfMonth element (-dd)
                we = weekyearElement(),  // weekyear element (xxxx)
                wwe = weekElement(), // weekOfWeekyear element (-ww)
                dwe = dayOfWeekElement(), // dayOfWeek element (-ee)
                dye = dayOfYearElement(), // dayOfYear element (-DDD)
                hde = hourElement(), // hourOfDay element (HH)
                mhe = minuteElement(), // minuteOfHour element (:mm)
                sme = secondElement(), // secondOfMinute element (:ss)
                fse = fractionElement(), // fractionOfSecond element (.SSSSSSSSS)
                ze = offsetElement(),  // zone offset element
                lte = literalTElement(), // literal 'T' element

        //y,   // year (same as year element)
        ym = yearMonth(),  // year month
                ymd = yearMonthDay(), // year month day

        //w,   // weekyear (same as weekyear element)
        ww = weekyearWeek(),  // weekyear week
                wwd = weekyearWeekDay(), // weekyear week day

        //h,    // hour (same as hour element)
        hm = hourMinute(),   // hour minute
                hms = hourMinuteSecond(),  // hour minute second
                hmsl = hourMinuteSecondMillis(), // hour minute second millis
                hmsf = hourMinuteSecondFraction(), // hour minute second fraction

        dh = dateHour(),    // date hour
                dhm = dateHourMinute(),   // date hour minute
                dhms = dateHourMinuteSecond(),  // date hour minute second
                dhmsl = dateHourMinuteSecondMillis(), // date hour minute second millis
                dhmsf = dateHourMinuteSecondFraction(), // date hour minute second fraction

        //d,  // date (same as ymd)
        t = time(),  // time
                tx = timeNoMillis(),  // time no millis
                tt = tTime(),  // Ttime
                ttx = tTimeNoMillis(),  // Ttime no millis
                dt = dateTime(), // date time
                dtx = dateTimeNoMillis(), // date time no millis

        //wd,  // week date (same as wwd)
        wdt = weekDateTime(), // week date time
                wdtx = weekDateTimeNoMillis(), // week date time no millis

        od = ordinalDate(),  // ordinal date (same as yd)
                odt = ordinalDateTime(), // ordinal date time
                odtx = ordinalDateTimeNoMillis(), // ordinal date time no millis

        bd = basicDate(),  // basic date
                bt = basicTime(),  // basic time
                btx = basicTimeNoMillis(),  // basic time no millis
                btt = basicTTime(), // basic Ttime
                bttx = basicTTimeNoMillis(), // basic Ttime no millis
                bdt = basicDateTime(), // basic date time
                bdtx = basicDateTimeNoMillis(), // basic date time no millis

        bod = basicOrdinalDate(),  // basic ordinal date
                bodt = basicOrdinalDateTime(), // basic ordinal date time
                bodtx = basicOrdinalDateTimeNoMillis(), // basic ordinal date time no millis

        bwd = basicWeekDate(),  // basic week date
                bwdt = basicWeekDateTime(), // basic week date time
                bwdtx = basicWeekDateTimeNoMillis(), // basic week date time no millis

        dpe = dateElementParser(), // date parser element
                tpe = timeElementParser(), // time parser element
                dp = dateParser(),  // date parser
                ldp = localDateParser(), // local date parser
                tp = timeParser(),  // time parser
                ltp = localTimeParser(), // local time parser
                dtp = dateTimeParser(), // date time parser
                dotp = dateOptionalTimeParser(), // date optional time parser
                ldotp = localDateOptionalTimeParser(); // local date optional time parser

        //-----------------------------------------------------------------------
        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateParser() {
            if (dp == null) {
                com.jn.langx.util.jodatime.format.DateTimeParser tOffset = new DateTimeFormatterBuilder()
                        .appendLiteral('T')
                        .append(offsetElement()).toParser();
                return new DateTimeFormatterBuilder()
                        .append(dateElementParser())
                        .appendOptional(tOffset)
                        .toFormatter();
            }
            return dp;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter localDateParser() {
            if (ldp == null) {
                return dateElementParser().withZoneUTC();
            }
            return ldp;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateElementParser() {
            if (dpe == null) {
                return new DateTimeFormatterBuilder()
                        .append(null, new com.jn.langx.util.jodatime.format.DateTimeParser[]{
                                new DateTimeFormatterBuilder()
                                        .append(yearElement())
                                        .appendOptional
                                                (new DateTimeFormatterBuilder()
                                                        .append(monthElement())
                                                        .appendOptional(dayOfMonthElement().getParser())
                                                        .toParser())
                                        .toParser(),
                                new DateTimeFormatterBuilder()
                                        .append(weekyearElement())
                                        .append(weekElement())
                                        .appendOptional(dayOfWeekElement().getParser())
                                        .toParser(),
                                new DateTimeFormatterBuilder()
                                        .append(yearElement())
                                        .append(dayOfYearElement())
                                        .toParser()
                        })
                        .toFormatter();
            }
            return dpe;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter timeParser() {
            if (tp == null) {
                return new DateTimeFormatterBuilder()
                        .appendOptional(literalTElement().getParser())
                        .append(timeElementParser())
                        .appendOptional(offsetElement().getParser())
                        .toFormatter();
            }
            return tp;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter localTimeParser() {
            if (ltp == null) {
                return new DateTimeFormatterBuilder()
                        .appendOptional(literalTElement().getParser())
                        .append(timeElementParser())
                        .toFormatter().withZoneUTC();
            }
            return ltp;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter timeElementParser() {
            if (tpe == null) {
                // Decimal point can be either '.' or ','
                com.jn.langx.util.jodatime.format.DateTimeParser decimalPoint = new DateTimeFormatterBuilder()
                        .append(null, new com.jn.langx.util.jodatime.format.DateTimeParser[]{
                                new DateTimeFormatterBuilder()
                                        .appendLiteral('.')
                                        .toParser(),
                                new DateTimeFormatterBuilder()
                                        .appendLiteral(',')
                                        .toParser()
                        })
                        .toParser();

                return new DateTimeFormatterBuilder()
                        // time-element
                        .append(hourElement())
                        .append
                                (null, new com.jn.langx.util.jodatime.format.DateTimeParser[]{
                                        new DateTimeFormatterBuilder()
                                                // minute-element
                                                .append(minuteElement())
                                                .append
                                                        (null, new com.jn.langx.util.jodatime.format.DateTimeParser[]{
                                                                new DateTimeFormatterBuilder()
                                                                        // second-element
                                                                        .append(secondElement())
                                                                        // second fraction
                                                                        .appendOptional(new DateTimeFormatterBuilder()
                                                                                .append(decimalPoint)
                                                                                .appendFractionOfSecond(1, 9)
                                                                                .toParser())
                                                                        .toParser(),
                                                                // minute fraction
                                                                new DateTimeFormatterBuilder()
                                                                        .append(decimalPoint)
                                                                        .appendFractionOfMinute(1, 9)
                                                                        .toParser(),
                                                                null
                                                        })
                                                .toParser(),
                                        // hour fraction
                                        new DateTimeFormatterBuilder()
                                                .append(decimalPoint)
                                                .appendFractionOfHour(1, 9)
                                                .toParser(),
                                        null
                                })
                        .toFormatter();
            }
            return tpe;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateTimeParser() {
            if (dtp == null) {
                // This is different from the general time parser in that the 'T'
                // is required.
                com.jn.langx.util.jodatime.format.DateTimeParser time = new DateTimeFormatterBuilder()
                        .appendLiteral('T')
                        .append(timeElementParser())
                        .appendOptional(offsetElement().getParser())
                        .toParser();
                return new DateTimeFormatterBuilder()
                        .append(null, new com.jn.langx.util.jodatime.format.DateTimeParser[]{time, dateOptionalTimeParser().getParser()})
                        .toFormatter();
            }
            return dtp;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateOptionalTimeParser() {
            if (dotp == null) {
                com.jn.langx.util.jodatime.format.DateTimeParser timeOrOffset = new DateTimeFormatterBuilder()
                        .appendLiteral('T')
                        .appendOptional(timeElementParser().getParser())
                        .appendOptional(offsetElement().getParser())
                        .toParser();
                return new DateTimeFormatterBuilder()
                        .append(dateElementParser())
                        .appendOptional(timeOrOffset)
                        .toFormatter();
            }
            return dotp;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter localDateOptionalTimeParser() {
            if (ldotp == null) {
                DateTimeParser time = new DateTimeFormatterBuilder()
                        .appendLiteral('T')
                        .append(timeElementParser())
                        .toParser();
                return new DateTimeFormatterBuilder()
                        .append(dateElementParser())
                        .appendOptional(time)
                        .toFormatter().withZoneUTC();
            }
            return ldotp;
        }

        //-----------------------------------------------------------------------
        private static com.jn.langx.util.jodatime.format.DateTimeFormatter time() {
            if (t == null) {
                return new DateTimeFormatterBuilder()
                        .append(hourMinuteSecondFraction())
                        .append(offsetElement())
                        .toFormatter();
            }
            return t;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter timeNoMillis() {
            if (tx == null) {
                return new DateTimeFormatterBuilder()
                        .append(hourMinuteSecond())
                        .append(offsetElement())
                        .toFormatter();
            }
            return tx;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter tTime() {
            if (tt == null) {
                return new DateTimeFormatterBuilder()
                        .append(literalTElement())
                        .append(time())
                        .toFormatter();
            }
            return tt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter tTimeNoMillis() {
            if (ttx == null) {
                return new DateTimeFormatterBuilder()
                        .append(literalTElement())
                        .append(timeNoMillis())
                        .toFormatter();
            }
            return ttx;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateTime() {
            if (dt == null) {
                return new DateTimeFormatterBuilder()
                        .append(date())
                        .append(tTime())
                        .toFormatter();
            }
            return dt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateTimeNoMillis() {
            if (dtx == null) {
                return new DateTimeFormatterBuilder()
                        .append(date())
                        .append(tTimeNoMillis())
                        .toFormatter();
            }
            return dtx;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter ordinalDate() {
            if (od == null) {
                return new DateTimeFormatterBuilder()
                        .append(yearElement())
                        .append(dayOfYearElement())
                        .toFormatter();
            }
            return od;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter ordinalDateTime() {
            if (odt == null) {
                return new DateTimeFormatterBuilder()
                        .append(ordinalDate())
                        .append(tTime())
                        .toFormatter();
            }
            return odt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter ordinalDateTimeNoMillis() {
            if (odtx == null) {
                return new DateTimeFormatterBuilder()
                        .append(ordinalDate())
                        .append(tTimeNoMillis())
                        .toFormatter();
            }
            return odtx;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter weekDateTime() {
            if (wdt == null) {
                return new DateTimeFormatterBuilder()
                        .append(weekDate())
                        .append(tTime())
                        .toFormatter();
            }
            return wdt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter weekDateTimeNoMillis() {
            if (wdtx == null) {
                return new DateTimeFormatterBuilder()
                        .append(weekDate())
                        .append(tTimeNoMillis())
                        .toFormatter();
            }
            return wdtx;
        }

        //-----------------------------------------------------------------------
        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicDate() {
            if (bd == null) {
                return new DateTimeFormatterBuilder()
                        .appendYear(4, 4)
                        .appendFixedDecimal(DateTimeFieldType.monthOfYear(), 2)
                        .appendFixedDecimal(DateTimeFieldType.dayOfMonth(), 2)
                        .toFormatter();
            }
            return bd;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicTime() {
            if (bt == null) {
                return new DateTimeFormatterBuilder()
                        .appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2)
                        .appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2)
                        .appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2)
                        .appendLiteral('.')
                        .appendFractionOfSecond(3, 9)
                        .appendTimeZoneOffset("Z", false, 2, 2)
                        .toFormatter();
            }
            return bt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicTimeNoMillis() {
            if (btx == null) {
                return new DateTimeFormatterBuilder()
                        .appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2)
                        .appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2)
                        .appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2)
                        .appendTimeZoneOffset("Z", false, 2, 2)
                        .toFormatter();
            }
            return btx;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicTTime() {
            if (btt == null) {
                return new DateTimeFormatterBuilder()
                        .append(literalTElement())
                        .append(basicTime())
                        .toFormatter();
            }
            return btt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicTTimeNoMillis() {
            if (bttx == null) {
                return new DateTimeFormatterBuilder()
                        .append(literalTElement())
                        .append(basicTimeNoMillis())
                        .toFormatter();
            }
            return bttx;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicDateTime() {
            if (bdt == null) {
                return new DateTimeFormatterBuilder()
                        .append(basicDate())
                        .append(basicTTime())
                        .toFormatter();
            }
            return bdt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicDateTimeNoMillis() {
            if (bdtx == null) {
                return new DateTimeFormatterBuilder()
                        .append(basicDate())
                        .append(basicTTimeNoMillis())
                        .toFormatter();
            }
            return bdtx;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicOrdinalDate() {
            if (bod == null) {
                return new DateTimeFormatterBuilder()
                        .appendYear(4, 4)
                        .appendFixedDecimal(DateTimeFieldType.dayOfYear(), 3)
                        .toFormatter();
            }
            return bod;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicOrdinalDateTime() {
            if (bodt == null) {
                return new DateTimeFormatterBuilder()
                        .append(basicOrdinalDate())
                        .append(basicTTime())
                        .toFormatter();
            }
            return bodt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicOrdinalDateTimeNoMillis() {
            if (bodtx == null) {
                return new DateTimeFormatterBuilder()
                        .append(basicOrdinalDate())
                        .append(basicTTimeNoMillis())
                        .toFormatter();
            }
            return bodtx;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicWeekDate() {
            if (bwd == null) {
                return new DateTimeFormatterBuilder()
                        .appendWeekyear(4, 4)
                        .appendLiteral('W')
                        .appendFixedDecimal(DateTimeFieldType.weekOfWeekyear(), 2)
                        .appendFixedDecimal(DateTimeFieldType.dayOfWeek(), 1)
                        .toFormatter();
            }
            return bwd;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicWeekDateTime() {
            if (bwdt == null) {
                return new DateTimeFormatterBuilder()
                        .append(basicWeekDate())
                        .append(basicTTime())
                        .toFormatter();
            }
            return bwdt;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter basicWeekDateTimeNoMillis() {
            if (bwdtx == null) {
                return new DateTimeFormatterBuilder()
                        .append(basicWeekDate())
                        .append(basicTTimeNoMillis())
                        .toFormatter();
            }
            return bwdtx;
        }

        //-----------------------------------------------------------------------
        private static com.jn.langx.util.jodatime.format.DateTimeFormatter yearMonth() {
            if (ym == null) {
                return new DateTimeFormatterBuilder()
                        .append(yearElement())
                        .append(monthElement())
                        .toFormatter();
            }
            return ym;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter yearMonthDay() {
            if (ymd == null) {
                return new DateTimeFormatterBuilder()
                        .append(yearElement())
                        .append(monthElement())
                        .append(dayOfMonthElement())
                        .toFormatter();
            }
            return ymd;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter weekyearWeek() {
            if (ww == null) {
                return new DateTimeFormatterBuilder()
                        .append(weekyearElement())
                        .append(weekElement())
                        .toFormatter();
            }
            return ww;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter weekyearWeekDay() {
            if (wwd == null) {
                return new DateTimeFormatterBuilder()
                        .append(weekyearElement())
                        .append(weekElement())
                        .append(dayOfWeekElement())
                        .toFormatter();
            }
            return wwd;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter hourMinute() {
            if (hm == null) {
                return new DateTimeFormatterBuilder()
                        .append(hourElement())
                        .append(minuteElement())
                        .toFormatter();
            }
            return hm;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter hourMinuteSecond() {
            if (hms == null) {
                return new DateTimeFormatterBuilder()
                        .append(hourElement())
                        .append(minuteElement())
                        .append(secondElement())
                        .toFormatter();
            }
            return hms;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter hourMinuteSecondMillis() {
            if (hmsl == null) {
                return new DateTimeFormatterBuilder()
                        .append(hourElement())
                        .append(minuteElement())
                        .append(secondElement())
                        .appendLiteral('.')
                        .appendFractionOfSecond(3, 3)
                        .toFormatter();
            }
            return hmsl;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter hourMinuteSecondFraction() {
            if (hmsf == null) {
                return new DateTimeFormatterBuilder()
                        .append(hourElement())
                        .append(minuteElement())
                        .append(secondElement())
                        .append(fractionElement())
                        .toFormatter();
            }
            return hmsf;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHour() {
            if (dh == null) {
                return new DateTimeFormatterBuilder()
                        .append(date())
                        .append(literalTElement())
                        .append(hour())
                        .toFormatter();
            }
            return dh;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHourMinute() {
            if (dhm == null) {
                return new DateTimeFormatterBuilder()
                        .append(date())
                        .append(literalTElement())
                        .append(hourMinute())
                        .toFormatter();
            }
            return dhm;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHourMinuteSecond() {
            if (dhms == null) {
                return new DateTimeFormatterBuilder()
                        .append(date())
                        .append(literalTElement())
                        .append(hourMinuteSecond())
                        .toFormatter();
            }
            return dhms;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHourMinuteSecondMillis() {
            if (dhmsl == null) {
                return new DateTimeFormatterBuilder()
                        .append(date())
                        .append(literalTElement())
                        .append(hourMinuteSecondMillis())
                        .toFormatter();
            }
            return dhmsl;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dateHourMinuteSecondFraction() {
            if (dhmsf == null) {
                return new DateTimeFormatterBuilder()
                        .append(date())
                        .append(literalTElement())
                        .append(hourMinuteSecondFraction())
                        .toFormatter();
            }
            return dhmsf;
        }

        //-----------------------------------------------------------------------
        private static com.jn.langx.util.jodatime.format.DateTimeFormatter yearElement() {
            if (ye == null) {
                return new DateTimeFormatterBuilder()
                        .appendYear(4, 9)
                        .toFormatter();
            }
            return ye;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter monthElement() {
            if (mye == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral('-')
                        .appendMonthOfYear(2)
                        .toFormatter();
            }
            return mye;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dayOfMonthElement() {
            if (dme == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral('-')
                        .appendDayOfMonth(2)
                        .toFormatter();
            }
            return dme;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter weekyearElement() {
            if (we == null) {
                return new DateTimeFormatterBuilder()
                        .appendWeekyear(4, 9)
                        .toFormatter();
            }
            return we;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter weekElement() {
            if (wwe == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral("-W")
                        .appendWeekOfWeekyear(2)
                        .toFormatter();
            }
            return wwe;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dayOfWeekElement() {
            if (dwe == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral('-')
                        .appendDayOfWeek(1)
                        .toFormatter();
            }
            return dwe;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter dayOfYearElement() {
            if (dye == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral('-')
                        .appendDayOfYear(3)
                        .toFormatter();
            }
            return dye;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter literalTElement() {
            if (lte == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral('T')
                        .toFormatter();
            }
            return lte;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter hourElement() {
            if (hde == null) {
                return new DateTimeFormatterBuilder()
                        .appendHourOfDay(2)
                        .toFormatter();
            }
            return hde;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter minuteElement() {
            if (mhe == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral(':')
                        .appendMinuteOfHour(2)
                        .toFormatter();
            }
            return mhe;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter secondElement() {
            if (sme == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral(':')
                        .appendSecondOfMinute(2)
                        .toFormatter();
            }
            return sme;
        }

        private static com.jn.langx.util.jodatime.format.DateTimeFormatter fractionElement() {
            if (fse == null) {
                return new DateTimeFormatterBuilder()
                        .appendLiteral('.')
                        // Support parsing up to nanosecond precision even though
                        // those extra digits will be dropped.
                        .appendFractionOfSecond(3, 9)
                        .toFormatter();
            }
            return fse;
        }

        private static DateTimeFormatter offsetElement() {
            if (ze == null) {
                return new DateTimeFormatterBuilder()
                        .appendTimeZoneOffset("Z", true, 2, 4)
                        .toFormatter();
            }
            return ze;
        }

    }

}