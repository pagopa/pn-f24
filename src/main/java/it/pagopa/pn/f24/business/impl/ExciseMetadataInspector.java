package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import static it.pagopa.pn.f24.util.Utility.countElementsByPredicate;

public class ExciseMetadataInspector implements MetadataInspector {
    @Override
    public int countMetadataApplyCost(F24Metadata f24Metadata) {
        F24Excise f24Excise = f24Metadata.getF24Excise();
        if (f24Excise == null) {
            return 0;
        }

        int applyCostCounter = 0;
        if(f24Excise.getTreasury() != null && f24Excise.getTreasury().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getTreasury().getRecords(), Tax::getApplyCost);
        }
        if(f24Excise.getInps() != null && f24Excise.getInps().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getInps().getRecords(), InpsRecord::getApplyCost);
        }
        if(f24Excise.getRegion() != null && f24Excise.getRegion().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getRegion().getRecords(), RegionRecord::getApplyCost);
        }
        if(f24Excise.getLocalTax() != null && f24Excise.getLocalTax().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getLocalTax().getRecords(), LocalTaxRecord::getApplyCost);
        }
        if(f24Excise.getExcise() != null && f24Excise.getExcise().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getExcise().getRecords(), ExciseTax::getApplyCost);
        }
        
        return applyCostCounter;
    }
}
