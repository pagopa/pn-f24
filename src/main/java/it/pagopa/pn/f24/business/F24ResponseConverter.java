package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.dto.safestorage.FileDownloadInfoInt;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Response;

public  class F24ResponseConverter {
    private F24ResponseConverter() { }
        public static F24Response fileDownloadInfoToF24Response(FileDownloadInfoInt fileDownloadResponseInt ) {
        F24Response f24Response = new F24Response();
        f24Response.setUrl(fileDownloadResponseInt.getUrl());
        f24Response.setRetryAfter(fileDownloadResponseInt.getRetryAfter());
        return f24Response;
    }
}
