package it.pagopa.pn.f24.business.impl;
import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import lombok.extern.slf4j.Slf4j;

import static it.pagopa.pn.f24.util.Utility.*;

@Slf4j
public class StandardMetadataInspector implements MetadataInspector {

        public int countMetadataApplyCost(F24Metadata f24Metadata) {
            F24Standard f24Standard = f24Metadata.getF24Standard();
            if (f24Standard == null) {
                return 0;
            }

            int applyCostCounter = 0;
            if(f24Standard.getTreasury() != null && f24Standard.getTreasury().getRecords() != null) {
                applyCostCounter += countElementsByPredicate(f24Standard.getTreasury().getRecords(), Tax::getApplyCost);
            }
            if(f24Standard.getInps() != null && f24Standard.getInps().getRecords() != null) {
                applyCostCounter += countElementsByPredicate(f24Standard.getInps().getRecords(), InpsRecord::getApplyCost);
            }
            if(f24Standard.getRegion() != null && f24Standard.getRegion().getRecords() != null) {
                applyCostCounter += countElementsByPredicate(f24Standard.getRegion().getRecords(), RegionRecord::getApplyCost);
            }
            if(f24Standard.getLocalTax() != null && f24Standard.getLocalTax().getRecords() != null) {
                applyCostCounter += countElementsByPredicate(f24Standard.getLocalTax().getRecords(), LocalTaxRecord::getApplyCost);
            }
            if(f24Standard.getSocialSecurity() != null) {
                if(f24Standard.getSocialSecurity().getRecords() != null) {
                    applyCostCounter += countElementsByPredicate(f24Standard.getSocialSecurity().getRecords(), InailRecord::getApplyCost);
                }
                if(f24Standard.getSocialSecurity().getSocSecRecords() != null) {
                    applyCostCounter += countElementsByPredicate(f24Standard.getSocialSecurity().getSocSecRecords(), SocialSecurityRecord::getApplyCost);
                }
            }

            return applyCostCounter;
        }

    @Override
    public void addCostToDebit(F24Metadata f24Metadata, Integer cost) {
        if (f24Metadata.getF24Standard() != null) {
            F24Standard f24Standard = f24Metadata.getF24Standard();
            for (Tax tax : f24Standard.getTreasury().getRecords()) {
                if (tax.getApplyCost()) {
                    tax.setDebit(MetadataInspector.convertDebitSum(tax.getDebit(), cost));
                    return;
                }
            }
            for (InpsRecord inpsRecord : f24Standard.getInps().getRecords()) {
                if (inpsRecord.getApplyCost()) {
                    inpsRecord.setDebit(MetadataInspector.convertDebitSum(inpsRecord.getDebit(), cost));
                    return;
                }
            }
            for (RegionRecord regionRecord : f24Standard.getRegion().getRecords()) {
                if (regionRecord.getApplyCost()) {
                    regionRecord.setDebit(MetadataInspector.convertDebitSum(regionRecord.getDebit(), cost));
                    return;
                }
            }
            for (LocalTaxRecord localTaxRecord : f24Standard.getLocalTax().getRecords()) {
                if (localTaxRecord.getApplyCost()) {
                    localTaxRecord.setDebit(MetadataInspector.convertDebitSum(localTaxRecord.getDebit(), cost));
                }
            }
            for (InailRecord inailRecord : f24Standard.getSocialSecurity().getRecords()) {
                if (inailRecord.getApplyCost()) {
                    inailRecord.setDebit(MetadataInspector.convertDebitSum(inailRecord.getDebit(), cost));
                }
            }
            for (SocialSecurityRecord socialSecurityRecord : f24Standard.getSocialSecurity().getSocSecRecords()) {
                if (socialSecurityRecord.getApplyCost()) {
                    socialSecurityRecord.setDebit(MetadataInspector.convertDebitSum(socialSecurityRecord.getDebit(), cost));
                }
            }

        }
    }
}
