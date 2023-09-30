package it.pagopa.pn.f24.business;

import static org.junit.Assert.assertNull;

import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadInfo;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Response;
import org.junit.Ignore;
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
     * Method under test: {@link F24ResponseConverter#fileDownloadInfoToF24Response(FileDownloadInfoInt)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testFileDownloadInfoToF24Response2() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt.getUrl()" because "fileDownloadResponseInt" is null
        //       at it.pagopa.pn.f24.business.F24ResponseConverter.fileDownloadInfoToF24Response(F24ResponseConverter.java:10)
        //   See https://diff.blue/R013 to resolve this issue.

        F24ResponseConverter.fileDownloadInfoToF24Response(null);
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

    /**
     * Method under test: {@link F24ResponseConverter#f24ResponseToFileDownloadInfo(F24Response)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testF24ResponseToFileDownloadInfo2() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Response.getUrl()" because "f24Response" is null
        //       at it.pagopa.pn.f24.business.F24ResponseConverter.f24ResponseToFileDownloadInfo(F24ResponseConverter.java:17)
        //   See https://diff.blue/R013 to resolve this issue.

        F24ResponseConverter.f24ResponseToFileDownloadInfo(null);
    }
}

