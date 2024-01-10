package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

import static it.pagopa.pn.f24.util.Utility.countElementsByPredicate;

public class SimplifiedMetadataInspector implements MetadataInspector {
    @Override
    public int countMetadataApplyCost(F24Metadata f24Metadata) {
        F24Simplified f24Simplified = f24Metadata.getF24Simplified();
        if (f24Simplified == null) {
            return 0;
        }

        int applyCostCounter = 0;
        if (f24Simplified.getPayments() != null && f24Simplified.getPayments().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Simplified.getPayments().getRecords(), SimplifiedPaymentRecord::getApplyCost);
        }

        return applyCostCounter;
    }
    @Override
    public double getTotalAmount(F24Metadata f24Metadata){
        double debit=0;
        for (int i = 0; i < f24Metadata.getF24Simplified().getPayments().getRecords().size(); i++) {
            debit= debit+f24Metadata.getF24Simplified().getPayments().getRecords().get(i).getDebit();
        }
        debit=debit/100;
        return debit;
    }
    public ApplyCostValidation checkApplyCost(F24Metadata f24Metadata, boolean requiredApplyCost) {
        F24Simplified f24Simplified = f24Metadata.getF24Simplified();
        if (f24Simplified == null) {
            return requiredApplyCost ? ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN : ApplyCostValidation.OK;
        }

        int validApplyCostFound = 0;
        int invalidApplyCostFound = 0;

        if (f24Simplified.getPayments() != null && f24Simplified.getPayments().getRecords() != null) {
            validApplyCostFound = countElementsByPredicate(f24Simplified.getPayments().getRecords(), payment -> payment.getApplyCost() && (payment.getCredit() == null || payment.getCredit() == 0));

            invalidApplyCostFound = countElementsByPredicate(f24Simplified.getPayments().getRecords(), payment -> payment.getApplyCost() && (payment.getCredit() != null && payment.getCredit() != 0));
        }

        return MetadataInspector.verifyApplyCost(requiredApplyCost, validApplyCostFound, invalidApplyCostFound);

    }
    @Override
    public void addCostToDebit(F24Metadata f24Metadata, Integer cost) {
        if (f24Metadata.getF24Simplified() != null) {
            F24Simplified f24Simplified = f24Metadata.getF24Simplified();
            for (SimplifiedPaymentRecord simplifiedPaymentRecord : f24Simplified.getPayments().getRecords()) {
                if (Boolean.TRUE.equals(simplifiedPaymentRecord.getApplyCost())) {
                    simplifiedPaymentRecord.setDebit(MetadataInspector.convertDebitSum(simplifiedPaymentRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }
}
