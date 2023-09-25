package it.pagopa.pn.f24.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private DateUtils(){}

    public static String formatInstantToString(Instant instantToFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
        return formatter.format(instantToFormat);
    }
}
