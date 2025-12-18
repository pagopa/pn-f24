package it.pagopa.pn.f24.it.mockbean;

import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.it.util.ThreadPool;
import it.pagopa.pn.f24.middleware.queue.consumer.service.ValidateMetadataEventService;
import it.pagopa.pn.f24.middleware.queue.producer.events.ValidateMetadataSetEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.awaitility.Awaitility.await;

@Slf4j
public class ValidateMetadataSetSqsProducerMock implements MomProducer<ValidateMetadataSetEvent>, ClearableMock {

    private final F24MetadataSetDaoMock f24MetadataSetDaoMock;
    private final ValidateMetadataEventService validateMetadataEventService;
    private boolean waitValidationApiCall = false;

    public ValidateMetadataSetSqsProducerMock(F24MetadataSetDaoMock f24MetadataSetDaoMock, ValidateMetadataEventService validateMetadataEventService) {
        this.f24MetadataSetDaoMock = f24MetadataSetDaoMock;
        this.validateMetadataEventService = validateMetadataEventService;
    }

    @Override
    public void push(List<ValidateMetadataSetEvent> list) {
        String setId = list.get(0).getPayload().getSetId();
        log.info("[TEST] pushing ValidateMetadataSetEvent with setId={} to queue", setId);

        ThreadPool.start(new Thread(() -> {
            Assertions.assertDoesNotThrow(() -> {
                try {
                    await().untilAsserted(() -> {
                        F24MetadataSet f24MetadataSet = f24MetadataSetDaoMock.getItem(setId).block();
                        if(waitValidationApiCall) {
                            log.info("[TEST] Start assertion for queue f24MetadataSet = {} should wait validationAPI call", f24MetadataSet);
                            Assertions.assertNotNull(f24MetadataSet);
                            Assertions.assertTrue(f24MetadataSet.getHaveToSendValidationEvent());
                        } else {
                            //Anche se non devo attendere la call dell API di validazione è necessario attendere che sia almeno persistito il metadata set da lavorare
                            log.info("[TEST] Start assertion for queue f24MetadataSet = {} should not wait validationAPI call", f24MetadataSet);
                            Assertions.assertNotNull(f24MetadataSet);
                        }
                    });
                    validateMetadataEventService.handleMetadataValidation(list.get(0).getPayload()).block();
                } catch (org.awaitility.core.ConditionTimeoutException ex) {
                    throw ex;
                }
            });
        }));

    }

    @Override
    public void push(List<ValidateMetadataSetEvent> list, Integer integer) {
        this.push(list);
    }

    @Override
    public void push(ValidateMetadataSetEvent validateMetadataSetEvent) {
        this.push(List.of(validateMetadataSetEvent));
    }

    @Override
    public void push(ValidateMetadataSetEvent validateMetadataSetEvent, Integer integer) {
        this.push(List.of(validateMetadataSetEvent));
    }

    public void setWaitValidationApiCall(boolean waitValidationApiCall) {
        this.waitValidationApiCall = waitValidationApiCall;
    }

    public void clear() {
        this.waitValidationApiCall = false;
    }
}
