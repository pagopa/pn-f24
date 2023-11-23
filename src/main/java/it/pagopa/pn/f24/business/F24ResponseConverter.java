package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.dto.safestorage.FileDownloadResponseInt;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Response;

public  class F24ResponseConverter {
    private F24ResponseConverter() { }
        public static F24Response fileDownloadInfoToF24Response(FileDownloadResponseInt fileDownloadResponseInt ) {
        F24Response f24Response = new F24Response();
        f24Response.setUrl(fileDownloadResponseInt.getDownload().getUrl());
        f24Response.setRetryAfter(fileDownloadResponseInt.getDownload().getRetryAfter());
        f24Response.setContentType(fileDownloadResponseInt.getContentType());
        f24Response.setContentLength(fileDownloadResponseInt.getContentLength());
        f24Response.setSha256(fileDownloadResponseInt.getChecksum());
        return f24Response;
    }
}
