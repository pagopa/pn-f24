package it.pagopa.pn.f24.f24lib.parser;


import it.pagopa.pn.f24.f24lib.util.LibTestException;

import java.util.function.Function;

public enum PdfFieldFormatter {
    DATE(s -> {
        if (s == null || s.length() != 8) {
            throw new LibTestException("Date format is not valid " + s);
        }
        return s.substring(0, 2) + "-" + s.substring(2, 4) + "-" + s.substring(4, 8);
    }),
    IMPORT(s -> {
        if (s == null) {
            throw new LibTestException("Import format is not valid " + s);
        }
        return s.replaceAll("\\s", "");
    }),
    NONE(s -> s);
    final Function<String, String> formatter;

    PdfFieldFormatter(Function<String, String> formatter) {
        this.formatter = formatter;
    }

    public String format(String s) {
        return formatter.apply(s);
    }
}
