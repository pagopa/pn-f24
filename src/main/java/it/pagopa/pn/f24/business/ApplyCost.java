package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

public class ApplyCost {

    public static void applyCost(F24Metadata metadata, Integer cost) {
        if (metadata.getF24Standard() != null) {
            F24Standard f24Standard = metadata.getF24Standard();
            for (Tax tax : f24Standard.getTreasury().getRecords()) {
                if (tax.getApplyCost()) {
                    tax.setDebit(convertDebitSum(tax.getDebit(), cost));
                    return;
                }
            }
            for (InpsRecord inpsRecord : f24Standard.getInps().getRecords()) {
                if (inpsRecord.getApplyCost()) {
                    inpsRecord.setDebit(convertDebitSum(inpsRecord.getDebit(), cost));
                    return;
                }
            }
            for (RegionRecord regionRecord : f24Standard.getRegion().getRecords()) {
                if (regionRecord.getApplyCost()) {
                    regionRecord.setDebit(convertDebitSum(regionRecord.getDebit(), cost));
                    return;
                }
            }
            for (LocalTaxRecord localTaxRecord : f24Standard.getLocalTax().getRecords()) {
                if (localTaxRecord.getApplyCost()) {
                    localTaxRecord.setDebit(convertDebitSum(localTaxRecord.getDebit(), cost));
                    return;
                }
            }
            for (InailRecord inailRecord : f24Standard.getSocialSecurity().getRecords()) {
                if (inailRecord.getApplyCost()) {
                    inailRecord.setDebit(convertDebitSum(inailRecord.getDebit(), cost));
                    return;
                }
            }
            for (SocialSecurityRecord socialSecurityRecord : f24Standard.getSocialSecurity().getSocSecRecords()) {
                if (socialSecurityRecord.getApplyCost()) {
                    socialSecurityRecord.setDebit(convertDebitSum(socialSecurityRecord.getDebit(), cost));
                    return;
                }
            }
        } else if (metadata.getF24Simplified() != null) {
            F24Simplified f24Simplified = metadata.getF24Simplified();
            for (SimplifiedPaymentRecord simplifiedPaymentRecord : f24Simplified.getPayments().getRecords()) {
                if (simplifiedPaymentRecord.getApplyCost()) {
                    simplifiedPaymentRecord.setDebit(convertDebitSum(simplifiedPaymentRecord.getDebit(), cost));
                }
            }
        } else if (metadata.getF24Elid() != null) {
            F24Elid f24Elid = metadata.getF24Elid();
            for (TreasuryRecord treasury : f24Elid.getTreasury().getRecords()) {
                if (treasury.getApplyCost()) {
                    treasury.setDebit(convertDebitSum(treasury.getDebit(), cost));
                }
            }
        } else if (metadata.getF24Excise() != null) {
            F24Excise f24Excise = metadata.getF24Excise();
            for (Tax tax : metadata.getF24Excise().getTreasury().getRecords()) {
                if (tax.getApplyCost()) {
                    tax.setDebit(convertDebitSum(tax.getDebit(), cost));
                }
            }
            for (InpsRecord inpsRecord : metadata.getF24Excise().getInps().getRecords()) {
                if (inpsRecord.getApplyCost()) {
                    inpsRecord.setDebit(convertDebitSum(inpsRecord.getDebit(), cost));
                }
            }
            for (RegionRecord regionRecord : metadata.getF24Excise().getRegion().getRecords()) {
                if (regionRecord.getApplyCost()) {
                    regionRecord.setDebit(convertDebitSum(regionRecord.getDebit(), cost));
                }
            }
            for (LocalTaxRecord localTaxRecord : metadata.getF24Excise().getLocalTax().getRecords()) {
                if (localTaxRecord.getApplyCost()) {
                    localTaxRecord.setDebit(convertDebitSum(localTaxRecord.getDebit(), cost));
                }
            }
            for (ExciseTax exciseTax : metadata.getF24Excise().getExcise().getRecords()) {
                if (exciseTax.getApplyCost()) {
                    exciseTax.setDebit(convertDebitSum(exciseTax.getDebit(), cost));
                }
            }
        }
    }

    public static String convertDebitSum(String debit, int cost) {
        int debitInt = Integer.parseInt(debit);
        debitInt = debitInt + cost;
        return String.valueOf(debitInt);
    }

}
