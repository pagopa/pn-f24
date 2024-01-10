package it.pagopa.pn.f24.f24lib.parser;

import it.pagopa.pn.f24.f24lib.exception.LibTestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.IOException;

@Slf4j
public class PdfParser {
    private final PDDocument doc;

    public PdfParser(byte[] pdf) {
        try {
            this.doc = PDDocument.load(pdf);
        } catch (IOException e) {
            throw new LibTestException("Couldn't parse generated pdf");
        }
    }

    private PDAcroForm getForm() {
        PDDocumentCatalog documentCatalog = this.doc.getDocumentCatalog();
        PDAcroForm form = documentCatalog.getAcroForm();
        if (form == null)
            throw new LibTestException("Form not found on PDF");
        return form;
    }

    public int numberOfPages() {
        return doc.getNumberOfPages();
    }

    public String getFieldValue(String fieldName)  {
        PDAcroForm acroForm = getForm();
        PDField field = acroForm.getField(fieldName);
        if (field instanceof PDTextField pdfTextField) {
            (pdfTextField).setActions(null);
        }

        if (field == null) {
            throw new LibTestException("Field " + fieldName + " not found in pdf form");
        }

        return field.getValueAsString();
    }

    public double getTotalAmountPdf() {
        double debit = 0;
        PDAcroForm acroForm = getForm();
        for (PDField field : acroForm.getFields()) {
            if (isFieldNotEmpty(field) && field.getPartialName() != null && field.getPartialName().equals("totalAmount")) {
                try {
                    String valueWithoutSpace = field.getValueAsString().replaceAll("\\s", "");
                    debit += Integer.parseInt(valueWithoutSpace);
                } catch (NumberFormatException e) {
                    throw new LibTestException("Couldn't format totalAmount value");
                }
            }
        }

        return debit / 100;
    }

    private static boolean isFieldNotEmpty(PDField campo) {
        return campo != null && !campo.getValueAsString().isEmpty();
    }

    public void closeDocument() {
        try {
            doc.close();
        } catch (IOException e) {
            throw new LibTestException("Couldn't close PDF doc.");
        }

    }
}