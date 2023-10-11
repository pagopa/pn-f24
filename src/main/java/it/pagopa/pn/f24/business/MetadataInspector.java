package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

public interface MetadataInspector {
    int countMetadataApplyCost(F24Metadata f24Metadata);


    void addCostToDebit(F24Metadata f24Metadata, Integer cost);

    ApplyCostValidation checkApplyCost(F24Metadata f24Metadata, boolean requiredApplyCost);


    static Integer convertDebitSum(Integer debit, int cost) {
        int debitInt = debit == null ? 0 : debit;
        debitInt = debitInt + cost;
        return debitInt;
    }

    static ApplyCostValidation verifyApplyCost(boolean requiredApplyCost, int validApplyCostCounter, int invalidApplyCostCounter) {
        if(invalidApplyCostCounter > 0) {
            return ApplyCostValidation.INVALID_APPLY_COST_GIVEN;
        }
        if(validApplyCostCounter > 0 && !requiredApplyCost) {
            return ApplyCostValidation.NOT_REQUIRED_APPLY_COST_GIVEN;
        }
        if(validApplyCostCounter == 0 && requiredApplyCost) {
            return ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN;
        }
        if(validApplyCostCounter > 1) {
            return ApplyCostValidation.TOO_MANY_APPLY_COST_GIVEN;
        }

        return ApplyCostValidation.OK;
    }
}
