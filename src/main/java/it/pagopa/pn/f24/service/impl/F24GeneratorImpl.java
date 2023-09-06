package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.f24.business.F24Converter;
import it.pagopa.pn.f24.business.F24ConverterFactory;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.service.F24Generator;
import lombok.extern.slf4j.Slf4j;
import org.f24.dto.form.F24Form;
import org.f24.exception.ResourceException;
import org.f24.service.pdf.PDFCreator;
import org.f24.service.pdf.PDFCreatorFactory;
import org.springframework.stereotype.Service;
import static it.pagopa.pn.f24.util.Utility.getF24TypeFromMetadata;

@Service
@Slf4j
public class F24GeneratorImpl implements F24Generator {
    @Override
    public byte[] generate(F24Metadata metadata) {
        F24Converter f24Converter = F24ConverterFactory.getConverter(getF24TypeFromMetadata(metadata));
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

}
