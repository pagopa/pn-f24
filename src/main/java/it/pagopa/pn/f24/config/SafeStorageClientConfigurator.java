package it.pagopa.pn.f24.config;

import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.ApiClient;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.api.FileDownloadApi;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.api.FileUploadApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SafeStorageClientConfigurator extends CommonBaseClient  {
    @Bean
    public FileDownloadApi downloadApiReactive(F24Config cfg){
        ApiClient apiClient = new ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getSafeStorageBaseUrl());
        return new FileDownloadApi(apiClient);
    }

    @Bean
    public FileUploadApi uploadApiReactive(F24Config cfg){
        ApiClient apiClient = new ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getSafeStorageBaseUrl());
        return new FileUploadApi(apiClient);
    }
}
