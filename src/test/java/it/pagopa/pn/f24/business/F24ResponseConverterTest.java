package it.pagopa.pn.f24.business;

import static org.junit.Assert.assertNull;

import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadInfo;
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

    /**
     * Method under test: {@link F24ResponseConverter#f24ResponseToFileDownloadInfo(F24Response)}
     */
    @Test
    public void testF24ResponseToFileDownloadInfo() {
        FileDownloadInfo actualF24ResponseToFileDownloadInfoResult = F24ResponseConverter
                .f24ResponseToFileDownloadInfo(new F24Response());
        assertNull(actualF24ResponseToFileDownloadInfoResult.getRetryAfter());
        assertNull(actualF24ResponseToFileDownloadInfoResult.getUrl());
    }

}

