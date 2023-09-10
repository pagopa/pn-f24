package it.pagopa.pn.f24.service.impl;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.api.dto.events.PnF24AsyncEvent;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.exception.PnBadRequestException;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.middleware.eventbus.EventBridgeProducer;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClientImpl;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;
import it.pagopa.pn.f24.service.F24Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {F24ServiceImpl.class})
@ExtendWith(SpringExtension.class)
class F24ServiceImplTest {
    @MockBean
    private EventBridgeProducer<PnF24AsyncEvent> eventBridgeProducer;

    @MockBean
    private F24Generator f24Generator;

    @MockBean
    private F24MetadataSetDao f24MetadataSetDao;

    @Autowired
    private F24ServiceImpl f24ServiceImpl;

    @MockBean
    private MomProducer<InternalMetadataEvent> momProducer;

    @MockBean
    private PnSafeStorageClientImpl pnSafeStorageClientImpl;

    /**
     * Method under test: {@link F24ServiceImpl#validate(String, String)}
     */
    @Test
    void testValidate() {
        when(f24MetadataSetDao.getItem(any(), any())).thenThrow(new PnBadRequestException(
                "An error occurred", "The characteristics of someone or something", "An error occurred"));
        assertThrows(PnBadRequestException.class, () -> f24ServiceImpl.validate("42", "42"));
        verify(f24MetadataSetDao).getItem(any(), any());
    }

    /**
     * Method under test: {@link F24ServiceImpl#validate(String, String)}
     */
    @Test
    void testValidate2() {
        when(f24MetadataSetDao.getItem(any(), any()))
                .thenReturn((Mono<F24MetadataSet>) mock(Mono.class));
        f24ServiceImpl.validate("42", "42");
        verify(f24MetadataSetDao).getItem(any(), any());
    }

    /**
     * Method under test: {@link F24ServiceImpl#generatePDF(String, String, String)}
     */
    @Test
    void testGeneratePDF() {
        assertNull(f24ServiceImpl.generatePDF("Iun", "Recipient Index", "Attachment Index"));
    }
}

