package it.pagopa.pn.f24.business;

import static org.junit.Assert.assertNull;

import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Response;
import org.junit.Test;

public class F24ResponseConverterTest {
    /**
     * Method under test: {@link F24ResponseConverter#fileDownloadInfoToF24Response(FileDownloadInfoInt)}
     */
    @Test
    public void testFileDownloadInfoToF24Response() {
        F24Response actualFileDownloadInfoToF24ResponseResult = F24ResponseConverter
                .fileDownloadInfoToF24Response(new FileDownloadInfoInt());
        assertNull(actualFileDownloadInfoToF24ResponseResult.getRetryAfter());
        assertNull(actualFileDownloadInfoToF24ResponseResult.getUrl());
    }

}

