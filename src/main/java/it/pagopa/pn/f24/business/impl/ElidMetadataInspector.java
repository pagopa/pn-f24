package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import static it.pagopa.pn.f24.util.Utility.countElementsByPredicate;

public class ElidMetadataInspector implements MetadataInspector {
    @Override
    public int countMetadataApplyCost(F24Metadata f24Metadata) {
        F24Elid f24Elid = f24Metadata.getF24Elid();
        if (f24Elid == null) {
            return 0;
        }

        int applyCostCounter = 0;
        if(f24Elid.getTreasury() != null && f24Elid.getTreasury().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Elid.getTreasury().getRecords(), TreasuryRecord::getApplyCost);
        }

        return applyCostCounter;
    }
}
