package it.pagopa.pn.f24.middleware.queue.consumer.service;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.generated.openapi.msclient.safestorage.model.FileDownloadResponse;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {SafeStorageEventService.class, F24Config.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SafeStorageEventServiceTest {
    @Autowired
    private F24Config f24Config;

    @MockBean
    private F24MetadataSetDao f24MetadataSetDao;

    @MockBean
    private MomProducer<ValidateMetadataSetEvent> momProducer;

    @Autowired
    private SafeStorageEventService safeStorageEventService;

    @Test
    public void testHandleSafeStorageResponse() {
        FileDownloadResponse response = mock(FileDownloadResponse.class);
        when(response.getDocumentType()).thenReturn("Document Type");
        when(response.getKey()).thenReturn("Key");
        safeStorageEventService.handleSafeStorageResponse(response);
        verify(response, atLeast(1)).getDocumentType();
        verify(response).getKey();
    }

}

