package it.pagopa.pn.f24.business;

import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;

public class ApplyCost {

    public static F24Metadata applyCost(F24Metadata metadata, Integer cost) {
        //todo:break as soon as applyCost is founded
        if (metadata.getF24Standard() != null) {
            F24Standard f24Standard = metadata.getF24Standard();
            //method to add cost to field debit of record for F24Standard
            for (Tax tax : f24Standard.getTreasury().getRecords()) {
                if (tax.getApplyCost()) {
                    tax.setDebit(convertDebitSum(tax.getDebit(), cost));
                }
            }
            for (InpsRecord inpsRecord : f24Standard.getInps().getRecords()) {
                if (inpsRecord.getApplyCost()) {
                    inpsRecord.setDebit(convertDebitSum(inpsRecord.getDebit(), cost));
                }
            }
            for (RegionRecord regionRecord : f24Standard.getRegion().getRecords()) {
                if (regionRecord.getApplyCost()) {
                    regionRecord.setDebit(convertDebitSum(regionRecord.getDebit(), cost));
                }
            }
            for (LocalTaxRecord localTaxRecord : f24Standard.getLocalTax().getRecords()) {
                if (localTaxRecord.getApplyCost()) {
                    localTaxRecord.setDebit(convertDebitSum(localTaxRecord.getDebit(), cost));
                }
            }
            for (InailRecord inailRecord : f24Standard.getSocialSecurity().getRecords()) {
                if (inailRecord.getApplyCost()) {
                    inailRecord.setDebit(convertDebitSum(inailRecord.getDebit(), cost));
                }
            }
            for (SocialSecurityRecord socialSecurityRecord : f24Standard.getSocialSecurity().getSocSecRecords()) {
                if (socialSecurityRecord.getApplyCost()) {
                    socialSecurityRecord.setDebit(convertDebitSum(socialSecurityRecord.getDebit(), cost));
                }
            }
        } else if (metadata.getF24Simplified() != null) {
            //todo: f24Simplified is missing inps field
            F24Simplified f24Simplified = metadata.getF24Simplified();
            //method to add cost to field debit of record for F24Simplified
        } else if (metadata.getF24Elid() != null) {
            F24Elid f24Elid = metadata.getF24Elid();
            //method to add cost to field debit of record for F24Elid
            for (TreasuryRecord treasury : f24Elid.getTreasury().getRecords()) {
                if (treasury.getApplyCost()) {
                    treasury.setDebit(convertDebitSum(treasury.getDebit(), cost));
                }
            }
        } else if (metadata.getF24Excise() != null) {
            F24Excise f24Excise = metadata.getF24Excise();
            //method to add cost to field debit of record for F24Excise
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
            return metadata;
        }
        return null;
    }

    public static String convertDebitSum(String debit, int cost) {
        int debitInt = Integer.parseInt(debit);
        debitInt = debitInt + cost;
        return String.valueOf(debitInt);
    }

}
