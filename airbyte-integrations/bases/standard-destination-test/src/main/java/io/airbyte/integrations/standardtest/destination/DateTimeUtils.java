/*
 * Copyright (c) 2021 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.standardtest.destination;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateTimeUtils {

  public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  public static final String DATE_FORMAT_PATTERN_2 = "yyyy-MM-dd";
  public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
  public static final DateFormat DATE_FORMAT_2 = new SimpleDateFormat(DATE_FORMAT_PATTERN_2);

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("[yyyy][yy]['-']['/']['.'][' '][MMM][MM][M]['-']['/']['.'][' '][dd][d]" +
          "[[' ']['T']HH:mm[':'ss[.][SSSSSS][SSSSS][SSSS][SSS][' '][z][zzz][Z][O][x][XXX][XX][X]]]");

  /**
   * Parse the Json date-time logical type to an Avro long value.
   *
   * @return the number of microseconds from the unix epoch, 1 January 1970 00:00:00.000000 UTC.
   */
  public static Long getEpochMicros(String jsonDateTime) {
    Instant instant = null;
    if (jsonDateTime.matches("-?\\d+")) {
      return Long.valueOf(jsonDateTime);
    }
    try {
      ZonedDateTime zdt = ZonedDateTime.parse(jsonDateTime, formatter);
      instant = zdt.toLocalDateTime().toInstant(ZoneOffset.of(zdt.getOffset().toString()));
    } catch (DateTimeParseException e) {
      try {
        LocalDateTime dt = LocalDateTime.parse(jsonDateTime, formatter);
        instant = dt.toInstant(ZoneOffset.UTC);
      } catch (DateTimeParseException ex) {
        // no logging since it may generate too much noise
      }
    }
    return instant == null ? null : instant.toEpochMilli() * 1000;
  }

  public static String getParsedSnowflake(String jsonDateTime) {
    Instant instant = null;
    try {
      ZonedDateTime zdt = ZonedDateTime.parse(jsonDateTime, formatter);
      instant = zdt.toLocalDateTime().atZone(ZoneId.systemDefault()).toLocalDateTime().toInstant(ZoneOffset.of(zdt.getOffset().toString()));
      return DATE_FORMAT.format(new Date(instant.toEpochMilli() + (instant.getNano() / 1000000)));
    } catch (DateTimeParseException e) {
      try {
        LocalDateTime dt = LocalDateTime.parse(jsonDateTime, formatter);
        instant = dt.minusNanos(dt.getNano()).toInstant(ZoneOffset.ofHours(-10));
      } catch (DateTimeParseException ex) {
        // no logging since it may generate too much noise
      }
    }
    return instant == null ? null : instant.toString();
  }

  public static String getParsedRedshift(String jsonDateTime) {
    Instant instant = null;
    try {

      ZonedDateTime zdt = ZonedDateTime.parse(jsonDateTime, formatter);
      instant = zdt.toLocalDateTime().toInstant(ZoneOffset.of(zdt.getOffset().toString()));
      return instant.toString().replace('T',' ').replace("Z", "+00").replaceAll("\\.\\d*", "." + zdt.getNano()/1000);
    } catch (DateTimeParseException e) {
      try {
        LocalDateTime dt = LocalDateTime.parse(jsonDateTime, formatter);
        instant = dt.toInstant(ZoneOffset.UTC);
        return instant.toString().replace('T',' ').replace("Z", "+00").replaceAll("\\.\\d*", "." + dt.getNano()/1000);
      } catch (DateTimeParseException ex) {
        // no logging since it may generate too much noise
      }
    }
    return instant == null ? null : instant.toString();
  }

  public static String getParsedPostgres(String jsonDateTime) {
    Instant instant = null;
    try {

      ZonedDateTime zdt = ZonedDateTime.parse(jsonDateTime, formatter);
      instant = zdt.toLocalDateTime().toInstant(ZoneOffset.of(zdt.getOffset().toString()));
      return instant.toString();
    } catch (DateTimeParseException e) {
      try {
        LocalDateTime dt = LocalDateTime.parse(jsonDateTime, formatter);
        instant = dt.toInstant(ZoneOffset.UTC);
        return instant.toString();
      } catch (DateTimeParseException ex) {
        // no logging since it may generate too much noise
      }
    }
    return instant == null ? null : instant.toString();
  }

  public static String getParsedMSSQL(String jsonDateTime) {
    Instant instant = null;
    try {

      ZonedDateTime zdt = ZonedDateTime.parse(jsonDateTime, formatter);
      instant = zdt.toLocalDateTime().toInstant(ZoneOffset.of(zdt.getOffset().toString()));
      return instant.toString().replace('T',' ').replace("Z", "").replaceAll("\\.\\d*", "." + (zdt.getNano()/1000000 == 0 ? "0" : (zdt.getNano()/1000 + 500)/1000));
    } catch (DateTimeParseException e) {
      try {
        LocalDateTime dt = LocalDateTime.parse(jsonDateTime, formatter);
        instant = dt.toInstant(ZoneOffset.UTC);
        return instant.toString().replace('T',' ').replace("Z", "").replaceAll("\\.\\d*", "." + (dt.getNano()/1000000 == 0 ? "0" : (dt.getNano()/1000 + 500)/1000));
      } catch (DateTimeParseException ex) {
        // no logging since it may generate too much noise
      }
    }
    return instant == null ? null : instant.toString();
  }

  public static String convertToDateFormatWithZeroTime(String jsonDate) {
    String epochDay = null;
    try {
      LocalDate date = LocalDate.parse(jsonDate, formatter);
      epochDay = DATE_FORMAT.format(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    } catch (DateTimeParseException e) {
      // no logging since it may generate too much noise
    }
    return epochDay;
  }

  public static String convertToDateFormat(String jsonDate) {
    String epochDay = null;
    try {
      LocalDate date = LocalDate.parse(jsonDate, formatter);
      epochDay = DATE_FORMAT_2.format(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    } catch (DateTimeParseException e) {
      // no logging since it may generate too much noise
    }
    return epochDay;
  }

  public static String convertToGeneralDateFormat(String jsonDate) {
    String epochDay = null;
    try {
      LocalDate date = LocalDate.parse(jsonDate, formatter);
      epochDay = date.toString();
    } catch (DateTimeParseException e) {
      // no logging since it may generate too much noise
    }
    return epochDay;
  }

}
