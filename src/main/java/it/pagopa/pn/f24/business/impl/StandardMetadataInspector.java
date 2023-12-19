package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.dto.ApplyCostValidation;
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
        if (f24Standard.getTreasury() != null && f24Standard.getTreasury().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Standard.getTreasury().getRecords(), Tax::getApplyCost);
        }
        if (f24Standard.getInps() != null && f24Standard.getInps().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Standard.getInps().getRecords(), InpsRecord::getApplyCost);
        }
        if (f24Standard.getRegion() != null && f24Standard.getRegion().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Standard.getRegion().getRecords(), RegionRecord::getApplyCost);
        }
        if (f24Standard.getLocalTax() != null && f24Standard.getLocalTax().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Standard.getLocalTax().getRecords(), LocalTaxRecord::getApplyCost);
        }
        if (f24Standard.getSocialSecurity() != null) {
            if (f24Standard.getSocialSecurity().getRecords() != null) {
                applyCostCounter += countElementsByPredicate(f24Standard.getSocialSecurity().getRecords(), InailRecord::getApplyCost);
            }
            if (f24Standard.getSocialSecurity().getSocSecRecords() != null) {
                applyCostCounter += countElementsByPredicate(f24Standard.getSocialSecurity().getSocSecRecords(), SocialSecurityRecord::getApplyCost);
            }
        }

        return applyCostCounter;
    }

    public ApplyCostValidation checkApplyCost(F24Metadata f24Metadata, boolean requiredApplyCost) {
        F24Standard f24Standard = f24Metadata.getF24Standard();
        if (f24Standard == null) {
            return requiredApplyCost ? ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN : ApplyCostValidation.OK;
        }

        int validApplyCostFound = 0;
        int invalidApplyCostFound = 0;

        if (f24Standard.getTreasury() != null && f24Standard.getTreasury().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Standard.getTreasury().getRecords(), tax -> tax.getApplyCost() && (tax.getCredit() == null || tax.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Standard.getTreasury().getRecords(), tax -> tax.getApplyCost() && (tax.getCredit() != null && tax.getCredit() > 0));
        }
        if (f24Standard.getInps() != null && f24Standard.getInps().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Standard.getInps().getRecords(), inps -> inps.getApplyCost() && (inps.getCredit() == null || inps.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Standard.getInps().getRecords(), inps -> inps.getApplyCost() && (inps.getCredit() != null && inps.getCredit() > 0));
        }
        if (f24Standard.getRegion() != null && f24Standard.getRegion().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Standard.getRegion().getRecords(), region -> region.getApplyCost() && (region.getCredit() == null || region.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Standard.getRegion().getRecords(), region -> region.getApplyCost() && (region.getCredit() != null && region.getCredit() > 0));
        }
        if (f24Standard.getLocalTax() != null && f24Standard.getLocalTax().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Standard.getLocalTax().getRecords(), localTax -> localTax.getApplyCost() && (localTax.getCredit() == null || localTax.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Standard.getLocalTax().getRecords(), localTax -> localTax.getApplyCost() && (localTax.getCredit() != null && localTax.getCredit() > 0));
        }
        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Standard.getSocialSecurity().getRecords(), socialRec -> socialRec.getApplyCost() && (socialRec.getCredit() == null || socialRec.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Standard.getSocialSecurity().getRecords(), socialRec -> socialRec.getApplyCost() && (socialRec.getCredit() != null && socialRec.getCredit() > 0));
        }
        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getSocSecRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Standard.getSocialSecurity().getSocSecRecords(), socSecRec -> socSecRec.getApplyCost() && (socSecRec.getCredit() == null || socSecRec.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Standard.getSocialSecurity().getSocSecRecords(), socSecRec -> socSecRec.getApplyCost() && (socSecRec.getCredit() != null && socSecRec.getCredit() > 0));
        }

        return MetadataInspector.verifyApplyCost(requiredApplyCost, validApplyCostFound, invalidApplyCostFound);
    }

    @Override
    public void addCostToDebit(F24Metadata f24Metadata, Integer cost) {
        if (f24Metadata.getF24Standard() != null) {
            F24Standard f24Standard = f24Metadata.getF24Standard();

            tryAddCostToTreasuryRecords(f24Standard, cost);

            tryAddCostToInpsRecords(f24Standard, cost);

            tryAddCostToRegionRecords(f24Standard, cost);

            tryAddCostToLocalTaxRecords(f24Standard, cost);

            tryAddCostToInailRecords(f24Standard, cost);

            tryAddCostToSocialSecurityRecords(f24Standard, cost);
        }
    }

    private void tryAddCostToTreasuryRecords(F24Standard f24Standard, Integer cost) {
        if (f24Standard.getTreasury() != null && f24Standard.getTreasury().getRecords() != null) {
            for (Tax tax : f24Standard.getTreasury().getRecords()) {
                if (tax.getApplyCost()) {
                    tax.setDebit(MetadataInspector.convertDebitSum(tax.getDebit(), cost));
                    return;
                }
            }
        }
    }

    @Override
    public double getTotalAmount(F24Metadata f24Metadata) {
        double debit = 0;
        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getTreasury() != null)
            debit = getDebitTreasury(f24Metadata.getF24Standard());

        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getInps() != null)
            debit = getDebitInps(f24Metadata.getF24Standard());

        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getRegion() != null)
            debit = getDebitRegion(f24Metadata.getF24Standard());

        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getLocalTax() != null)
            debit = getDebitLocalTax(f24Metadata.getF24Standard());

        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getSocialSecurity() != null)
            debit = getDebitSocialSecurityINAIL(f24Metadata.getF24Standard());

        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getSocialSecurity().getSocSecRecords() != null)
            debit = getDebitSocialSecurityRecords(f24Metadata.getF24Standard());
        debit = debit / 100;
        return debit;
    }

    private double getDebitSocialSecurityINAIL(F24Standard f24Standard) {
        int debit = 0;
        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getRecords() != null) {
            for (int i = 0; i < f24Standard.getSocialSecurity().getRecords().size(); i++) {
                debit = debit + f24Standard.getSocialSecurity().getRecords().get(i).getDebit();
            }
        }
        return debit;
    }
    private double getDebitSocialSecurityRecords(F24Standard f24Standard) {
        int debit = 0;
        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getSocSecRecords() != null) {
            for (int i = 0; i < f24Standard.getSocialSecurity().getSocSecRecords().size(); i++) {
                debit = debit + f24Standard.getSocialSecurity().getSocSecRecords().get(i).getDebit();
            }
        }
        return debit;
    }

    private double getDebitTreasury(F24Standard f24Standard) {
        int debit = 0;
        if (f24Standard.getTreasury() != null && f24Standard.getTreasury().getRecords() != null) {
            for (int i = 0; i < f24Standard.getTreasury().getRecords().size(); i++) {
                if (f24Standard.getTreasury().getRecords().get(i).getDebit() == null) {
                    debit = debit + f24Standard.getInps().getRecords().get(i).getDebit();
                }
            }
        }
        return debit;
    }

    private double getDebitInps(F24Standard f24Standard) {
        int debit = 0;
        if (f24Standard.getInps() != null && f24Standard.getInps().getRecords() != null) {
            for (int i = 0; i < f24Standard.getInps().getRecords().size(); i++) {
                debit = debit + f24Standard.getInps().getRecords().get(i).getDebit();
            }
        }
        return debit;
    }

    private double getDebitRegion(F24Standard f24Standard) {
        int debit = 0;
        if (f24Standard.getRegion() != null && f24Standard.getRegion().getRecords() != null) {
            for (int i = 0; i < f24Standard.getRegion().getRecords().size(); i++) {
                debit = debit + f24Standard.getRegion().getRecords().get(i).getDebit();
            }
        }
        return debit;
    }

    private double getDebitLocalTax(F24Standard f24Standard) {
        int debit = 0;
        if (f24Standard.getLocalTax() != null && f24Standard.getLocalTax().getRecords() != null) {
            for (int i = 0; i < f24Standard.getLocalTax().getRecords().size(); i++) {
                debit = debit + f24Standard.getLocalTax().getRecords().get(i).getDebit();
            }
        }
        return debit;
    }


    private void tryAddCostToInpsRecords(F24Standard f24Standard, Integer cost) {
        if (f24Standard.getInps() != null && f24Standard.getInps().getRecords() != null) {
            for (InpsRecord inpsRecord : f24Standard.getInps().getRecords()) {
                if (inpsRecord.getApplyCost()) {
                    inpsRecord.setDebit(MetadataInspector.convertDebitSum(inpsRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }

    private void tryAddCostToRegionRecords(F24Standard f24Standard, Integer cost) {
        if (f24Standard.getRegion() != null && f24Standard.getRegion().getRecords() != null) {
            for (RegionRecord regionRecord : f24Standard.getRegion().getRecords()) {
                if (regionRecord.getApplyCost()) {
                    regionRecord.setDebit(MetadataInspector.convertDebitSum(regionRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }

    private void tryAddCostToLocalTaxRecords(F24Standard f24Standard, Integer cost) {
        if (f24Standard.getLocalTax() != null && f24Standard.getLocalTax().getRecords() != null) {
            for (LocalTaxRecord localTaxRecord : f24Standard.getLocalTax().getRecords()) {
                if (Boolean.TRUE.equals(localTaxRecord.getApplyCost())) {
                    localTaxRecord.setDebit(MetadataInspector.convertDebitSum(localTaxRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }

    private void tryAddCostToInailRecords(F24Standard f24Standard, Integer cost) {
        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getRecords() != null) {
            for (InailRecord inailRecord : f24Standard.getSocialSecurity().getRecords()) {
                if (inailRecord.getApplyCost()) {
                    inailRecord.setDebit(MetadataInspector.convertDebitSum(inailRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }

    private void tryAddCostToSocialSecurityRecords(F24Standard f24Standard, Integer cost) {
        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getSocSecRecords() != null) {
            for (SocialSecurityRecord socialSecurityRecord : f24Standard.getSocialSecurity().getSocSecRecords()) {
                if (socialSecurityRecord.getApplyCost()) {
                    socialSecurityRecord.setDebit(MetadataInspector.convertDebitSum(socialSecurityRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }
}
