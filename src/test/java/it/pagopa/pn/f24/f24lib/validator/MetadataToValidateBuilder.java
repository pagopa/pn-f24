package it.pagopa.pn.f24.f24lib.validator;

import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.MetadataToValidate;
import it.pagopa.pn.f24.f24lib.util.LibTestException;
import it.pagopa.pn.f24.util.Sha256Handler;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

public class MetadataToValidateBuilder {
    private static final String METADATA_FOLDER_PATH = "src/test/resources/metadata/";


    public static MetadataToValidate metaBuilder(String name, Boolean applyCost) {

        MetadataToValidate metadataToValidate = new MetadataToValidate();
        F24MetadataRef f24MetadataRef = new F24MetadataRef();

        metadataToValidate.setMetadataFile(getMetadataByName(name));

        f24MetadataRef.setApplyCost(applyCost);
        f24MetadataRef.setSha256(Sha256Handler.computeSha256(metadataToValidate.getMetadataFile()));
        f24MetadataRef.setFileKey(name);

        metadataToValidate.setRef(f24MetadataRef);
        metadataToValidate.setPathTokensKey("pathTokensKey");

        return metadataToValidate;
    }

    private static byte[] getMetadataByName(String fileName) {
        Resource fileResource = new FileSystemResource(METADATA_FOLDER_PATH + fileName);

        byte[] fileBytes;
        try {
            InputStream inputStream = fileResource.getInputStream();
            fileBytes = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new LibTestException("Couldn't find the metadata " + fileName + " specified in folder " + METADATA_FOLDER_PATH);
        }
        return fileBytes;
    }
}
