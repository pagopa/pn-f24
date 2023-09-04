package it.pagopa.pn.f24.service.impl;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.dto.metadata.F24Item;
import it.pagopa.pn.f24.dto.F24Type;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.factory.*;
import lombok.extern.slf4j.Slf4j;
import org.f24.dto.form.F24Form;
import org.f24.exception.ResourceException;
import org.f24.service.pdf.PDFCreator;
import org.f24.service.pdf.PDFCreatorFactory;
import org.f24.service.validator.Validator;
import org.f24.service.validator.ValidatorFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static it.pagopa.pn.f24.exception.PnF24ExceptionCodes.ERROR_CODE_VALIDATE;
import static it.pagopa.pn.f24.exception.PnF24ExceptionCodes.ERROR_MESSAGE_INVALID_METADATA;

@Service
@Slf4j
public class F24GeneratorImpl implements F24Generator {
    private final F24ConverterFactory f24ConverterFactory;

    public F24GeneratorImpl(F24ConverterFactory f24ConverterFactory) {
        this.f24ConverterFactory = f24ConverterFactory;
    }

    @Override
    public void validate(F24Item metadata) {
        log.info("validating f24 metadata");
        F24Converter f24Converter = f24ConverterFactory.getConverter(getF24TypeFromItem(metadata));
        F24Form f24Form = f24Converter.convert(metadata);
        try {
            Validator validator = ValidatorFactory.createValidator(f24Form);
            validator.validate();
        } catch (ResourceException | IOException | ProcessingException e) {
            throw new PnBadRequestException(String.format(ERROR_MESSAGE_INVALID_METADATA, metadata.getPathTokens().get(0), metadata.getPathTokens().get(1)), "", ERROR_CODE_VALIDATE);
        }
    }

    @Override
    public byte[] generate(F24Item metadata) {
        F24Converter f24Converter = f24ConverterFactory.getConverter(getF24TypeFromItem(metadata));
        F24Form f24Form = f24Converter.convert(metadata);
        byte[] generatedPdf;
        try {
            PDFCreator pdfCreator = PDFCreatorFactory.createPDFCreator(f24Form);
            generatedPdf = pdfCreator.createPDF();
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        }
        return generatedPdf;
    }

    private F24Type getF24TypeFromItem(F24Item f24Item) {
        if(f24Item.getF24Standard() != null) {
            return F24Type.F24_STANDARD;
        } else if(f24Item.getF24Simplified() != null) {
            return F24Type.F24_SIMPLIFIED;
        } else if(f24Item.getF24Elid() != null) {
            return F24Type.F24_ELID;
        } else if(f24Item.getF24Excise() != null) {
            return F24Type.F24_EXCISE;
        }

        throw new RuntimeException("Invalid F24 Type");
    }

}
