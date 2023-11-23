package it.pagopa.pn.f24.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Response;
import org.junit.Test;

import java.math.BigDecimal;

public class F24ResponseConverterTest {
    /**
     * Method under test: {@link F24ResponseConverter#fileDownloadInfoToF24Response(FileDownloadInfoInt)}
     */
    @Test
    public void testFileDownloadInfoToF24Response() {
        FileDownloadResponseInt f = new FileDownloadResponseInt();
        f.setDownload(new FileDownloadInfoInt("url", new BigDecimal(0)));
        f.setChecksum("123");
        f.setContentType("application/pdf");
        f.setContentLength(new BigDecimal(100));

        F24Response actualFileDownloadInfoToF24ResponseResult = F24ResponseConverter
                .fileDownloadInfoToF24Response(f);
        assertEquals(f.getDownload().getRetryAfter(), actualFileDownloadInfoToF24ResponseResult.getRetryAfter());
        assertEquals(f.getDownload().getUrl(), actualFileDownloadInfoToF24ResponseResult.getUrl());
        assertEquals(f.getContentLength(), actualFileDownloadInfoToF24ResponseResult.getContentLength());
        assertEquals(f.getContentType(), actualFileDownloadInfoToF24ResponseResult.getContentType());
        assertEquals(f.getChecksum(), actualFileDownloadInfoToF24ResponseResult.getSha256());

    }

}

