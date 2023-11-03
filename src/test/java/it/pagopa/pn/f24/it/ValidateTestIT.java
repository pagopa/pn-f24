package it.pagopa.pn.f24.it;

import it.pagopa.pn.api.dto.events.PnF24MetadataValidationEndEvent;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.dto.F24MetadataStatus;
import it.pagopa.pn.f24.it.mockbean.F24FileCacheDaoMock;
import it.pagopa.pn.f24.it.mockbean.F24MetadataSetDaoMock;
import it.pagopa.pn.f24.it.mockbean.MetadataValidationEndedEventProducerMock;
import it.pagopa.pn.f24.middleware.dao.f24file.dynamo.F24FileCacheRepositoryImpl;
import it.pagopa.pn.f24.middleware.eventbus.util.PnF24AsyncEventBuilderHelper;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.time.Instant;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        F24FileCacheDaoMock.class,
        F24FileCacheRepositoryImpl.class,
})
@DirtiesContext
public class ValidateTestIT {

    @Autowired
    private F24MetadataSetDaoMock f24MetadataSetDaoMock;

    @Autowired
    private MetadataValidationEndedEventProducerMock metadataValidationEndedEventProducerMock;

    @Test
    public void validate() {

        F24MetadataSetDaoMock f24MetadataSetDaoMock = new F24MetadataSetDaoMock();
        F24MetadataSet f24MetadataSet = f24MetadataSetDaoMock.getItem("setId").block();
        Assertions.assertNotNull(f24MetadataSet);
    }

    private Mono<Void> handleMetadataToValidate(F24MetadataSet f24MetadataSet, String cxId) {

        return updateHaveToSendValidationEventAndRemoveTtl(f24MetadataSet, cxId)
                .flatMap(this::checkIfValidationOnQueueHasEnded);
    }

    private Mono<F24MetadataSet> updateHaveToSendValidationEventAndRemoveTtl(F24MetadataSet f24MetadataSet, String cxId) {
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setTtl(null);
        f24MetadataSet.setValidatorCxId(cxId);
        return f24MetadataSetDaoMock.updateItem(f24MetadataSet);
    }

    private Mono<Void> checkIfValidationOnQueueHasEnded(F24MetadataSet f24MetadataSet) {
        String setId = f24MetadataSet.getSetId();

        return f24MetadataSetDaoMock.getItem(setId, true)
                .flatMap(f24MetadataSetConsistent -> {
                    if (f24MetadataSetConsistent.getStatus().equals(F24MetadataStatus.VALIDATION_ENDED)) {
                        return sendValidationEndedEventAndUpdateMetadataSet(f24MetadataSetConsistent, f24MetadataSet.getValidatorCxId());
                    }

                    return Mono.empty();
                });
    }

    private Mono<Void> sendValidationEndedEventAndUpdateMetadataSet(F24MetadataSet f24MetadataSet, String validatorCxId) {
        String setId = f24MetadataSet.getSetId();

        PnF24MetadataValidationEndEvent event = PnF24AsyncEventBuilderHelper.buildMetadataValidationEndEvent(validatorCxId, setId, f24MetadataSet.getValidationResult());
        return metadataValidationEndedEventProducerMock.sendEvent(event)
                .then(Mono.defer(() -> setValidationEventSentOnMetadataSet(f24MetadataSet, validatorCxId)));
    }

    private Mono<Void> setValidationEventSentOnMetadataSet(F24MetadataSet f24MetadataSet, String validatorCxId) {
        f24MetadataSet.setHaveToSendValidationEvent(true);
        f24MetadataSet.setValidationEventSent(true);
        f24MetadataSet.setUpdated(Instant.now());
        f24MetadataSet.setTtl(null);
        f24MetadataSet.setValidatorCxId(validatorCxId);
        return f24MetadataSetDaoMock.updateItem(f24MetadataSet)
                .then();
    }

}
