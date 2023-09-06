package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Simplified;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.SimplifiedPaymentRecord;

import static it.pagopa.pn.f24.util.Utility.countElementsByPredicate;

public class SimplifiedMetadataInspector implements MetadataInspector {
    @Override
    public int countMetadataApplyCost(F24Metadata f24Metadata) {
        F24Simplified f24Simplified = f24Metadata.getF24Simplified();
        if (f24Simplified == null) {
            return 0;
        }

        int applyCostCounter = 0;
        if(f24Simplified.getPayments() != null && f24Simplified.getPayments().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Simplified.getPayments().getRecords(), SimplifiedPaymentRecord::getApplyCost);
        }

        return applyCostCounter;
    }
}
