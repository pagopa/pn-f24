package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

public interface MetadataInspector {
    int countMetadataApplyCost(F24Metadata f24Metadata);


    void addCostToDebit(F24Metadata f24Metadata , Integer cost);


     static String convertDebitSum(String debit, int cost) {
        int debitInt = Integer.parseInt(debit);
        debitInt = debitInt + cost;
        return String.valueOf(debitInt);
    }
}
