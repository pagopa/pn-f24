package it.pagopa.pn.f24.middleware.queue.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Item;
import it.pagopa.pn.f24.middleware.dao.f24metadatadao.F24MetadataDao;
import it.pagopa.pn.f24.middleware.eventbus.MetadataValidationEventProducer;
import it.pagopa.pn.f24.middleware.msclient.safestorage.PnSafeStorageClient;
import it.pagopa.pn.f24.middleware.queue.producer.InternalMetadataEvent;
import it.pagopa.pn.f24.service.F24Generator;
import it.pagopa.pn.f24.service.SafeStorageService;
import lombok.CustomLog;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@CustomLog
public class ValidateMetadataEventService {

    private final SafeStorageService safeStorageService;
    private final F24MetadataDao f24MetadataDao;
    private final PnSafeStorageClient pnSafeStorageClient;
    private final F24Generator f24Generator;

    private final MetadataValidationEventProducer metadataValidationEventProducer;

    public ValidateMetadataEventService(SafeStorageService safeStorageService, F24MetadataDao f24MetadataDao, PnSafeStorageClient pnSafeStorageClient, F24Generator f24Generator, MetadataValidationEventProducer metadataValidationEventProducer) {
        this.safeStorageService = safeStorageService;
        this.f24MetadataDao = f24MetadataDao;
        this.pnSafeStorageClient = pnSafeStorageClient;
        this.f24Generator = f24Generator;
        this.metadataValidationEventProducer = metadataValidationEventProducer;
    }

    public Mono<Void> handleValidateMetadata(InternalMetadataEvent.Payload payload) {
        String setId = payload.getSetId();
        String cxId = payload.getCxId();
        log.info("handle save metadata with setId {} and cxId {}", setId, cxId);
        final String processName = "VALIDATE METADATA HANDLER";
        log.logStartingProcess(processName);

        return f24MetadataDao.getItem(setId, cxId)
                .flatMap(f24Metadata ->
                    safeStorageService.getFile(f24Metadata.getFileKey(), false)
                        .map(fileDownloadResponseInt -> {
                            byte[] bytes = pnSafeStorageClient.downloadPieceOfContent(fileDownloadResponseInt.getDownload().getUrl(), -1);
                            String fileContent = new String(bytes, StandardCharsets.UTF_8);
                            List<F24Item> f24Items = jsonStringToObject(fileContent);
                            f24Items.forEach(f24Generator::validate);
                            metadataValidationEventProducer.sendMetadataValidationEvent();
                            return f24Metadata;
                        })
                ).then();

    }

    private List<F24Item> jsonStringToObject(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<List<F24Item>>() { });
        } catch (JsonProcessingException e) {
            //TODO Gestire eccezione
            throw new RuntimeException(e);
        }
    }
}
