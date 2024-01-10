package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.dto.ApplyCostValidation;
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
        if (f24Elid.getTreasury() != null && f24Elid.getTreasury().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Elid.getTreasury().getRecords(), TreasuryRecord::getApplyCost);
        }

        return applyCostCounter;
    }

    public ApplyCostValidation checkApplyCost(F24Metadata f24Metadata, boolean requiredApplyCost) {
        F24Elid f24Elid = f24Metadata.getF24Elid();
        if (f24Elid == null) {
            return requiredApplyCost ? ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN : ApplyCostValidation.OK;
        }

        int validApplyCostFound = 0;
        int invalidApplyCostFound = 0;

        if (f24Elid.getTreasury() != null && f24Elid.getTreasury().getRecords() != null) {
            validApplyCostFound = countElementsByPredicate(f24Elid.getTreasury().getRecords(), TreasuryRecord::getApplyCost);
        }

        return MetadataInspector.verifyApplyCost(requiredApplyCost, validApplyCostFound, invalidApplyCostFound);

    }
    @Override
    public double getTotalAmount(F24Metadata f24Metadata){
        double debit=0;
        for (int i = 0; i < f24Metadata.getF24Elid().getTreasury().getRecords().size(); i++) {
            debit= debit+f24Metadata.getF24Elid().getTreasury().getRecords().get(i).getDebit();
        }
        debit=debit/100;
        return debit;
    }

    @Override
    public void addCostToDebit(F24Metadata f24Metadata, Integer cost) {
        if (f24Metadata.getF24Elid() != null) {
            F24Elid f24Elid = f24Metadata.getF24Elid();
            for (TreasuryRecord treasury : f24Elid.getTreasury().getRecords()) {
                if (treasury.getApplyCost()) {
                    treasury.setDebit(MetadataInspector.convertDebitSum(treasury.getDebit(), cost));
                    return;
                }
            }
        }
    }
}
