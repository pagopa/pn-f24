package it.pagopa.pn.f24.service.impl;

import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.dto.F24MetadataRef;
import it.pagopa.pn.f24.dto.F24MetadataSet;
import it.pagopa.pn.f24.exception.PnNotFoundException;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Standard;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.Tax;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.TreasurySection;
import it.pagopa.pn.f24.middleware.dao.f24metadataset.F24MetadataSetDao;
import it.pagopa.pn.f24.service.MetadataDownloader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {F24ParserServiceImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties(value = F24Config.class)
public class F24ParserServiceImplTest {
    @MockitoBean
    private F24MetadataSetDao f24MetadataSetDao;

    @MockitoBean
    private MetadataDownloader metadataDownloader;

    @Autowired
    private F24ParserServiceImpl f24ParserService;

    @Test
    void getTotalPagesFromMetadataSetFails() {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("test");
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setFileKey("METADATA_1");
        Map<String, F24MetadataRef> fileKeysMap = Map.of("0_0", f24MetadataRef);
        f24MetadataSet.setFileKeys(fileKeysMap);

        when(f24MetadataSetDao.getItem(any())).thenReturn(Mono.empty());

        StepVerifier.create(f24ParserService.getTotalPagesFromMetadataSet("test", List.of("0")))
                .expectError(PnNotFoundException.class)
                .verify();
    }

    @Test
    void getTotalPagesFromMetadataSetSucceeds() {
        F24MetadataSet f24MetadataSet = new F24MetadataSet();
        f24MetadataSet.setSetId("test");
        F24MetadataRef f24MetadataRef = new F24MetadataRef();
        f24MetadataRef.setApplyCost(true);
        f24MetadataRef.setSha256("sha256");
        f24MetadataRef.setFileKey("METADATA_1");
        Map<String, F24MetadataRef> fileKeysMap = Map.of("0_0", f24MetadataRef);
        f24MetadataSet.setFileKeys(fileKeysMap);

        when(f24MetadataSetDao.getItem(any())).thenReturn(Mono.just(f24MetadataSet));
        when(metadataDownloader.downloadMetadata(any())).thenReturn(getMetadataWithOneRecord());

        StepVerifier.create(f24ParserService.getTotalPagesFromMetadataSet("test", List.of("0")))
                .expectNextMatches(next -> next.getF24Set().size() == 1 && next.getF24Set().get(0).getNumberOfPages() == 3)
                .verifyComplete();
    }

    private Mono<F24Metadata> getMetadataWithOneRecord() {
        F24Metadata f24Metadata = new F24Metadata();
        F24Standard f24Standard = new F24Standard();
        TreasurySection treasurySection = new TreasurySection();
        treasurySection.setRecords(List.of(new Tax()));
        f24Standard.setTreasury(treasurySection);
        f24Metadata.setF24Standard(f24Standard);
        return Mono.just(f24Metadata);
    }
}
