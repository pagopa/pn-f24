package it.pagopa.pn.f24.f24lib.parser;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import it.pagopa.pn.f24.f24lib.exception.LibTestException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class PdfParser {
    private final PdfReader doc;

    public PdfParser(byte[] pdf) {
        try {
            this.doc = new PdfReader(pdf);
        } catch (IOException e) {
            throw new LibTestException("Couldn't parse generated pdf");
        }
    }

    private AcroFields getForm() {
        AcroFields form = doc.getAcroFields();
        if (form == null) {
            throw new LibTestException("Form not found on PDF");
        }
        return form;
    }

    public int numberOfPages() {
        return doc.getNumberOfPages();
    }

    public String getFieldValue(String fieldName) {
        AcroFields form = getForm();
        String value = form.getField(fieldName);
        if (value == null) {
            throw new LibTestException("Field " + fieldName + " not found in pdf form");
        }
        return value;
    }

    public double getTotalAmountPdf() {
        double debit = 0;
        AcroFields form = getForm();
        try {
            String valueWithoutSpace = form.getField("totalAmount").replaceAll("\\s", "");
            debit += Integer.parseInt(valueWithoutSpace);
        } catch (NumberFormatException e) {
            throw new LibTestException("Couldn't format totalAmount value");
        }
        return debit / 100;
    }

    public void closeDocument() {
        doc.close();
    }
}