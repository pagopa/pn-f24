package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.dto.ApplyCostValidation;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.*;
import lombok.extern.slf4j.Slf4j;

import static it.pagopa.pn.f24.util.Utility.*;
import static org.f24.service.pdf.util.FieldEnum.*;

@Slf4j
public class StandardMetadataInspector implements MetadataInspector {

    private static final int STANDARD_DEFAULT_NUMBER_OF_COPIES = 3;

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
        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getTreasury() != null) {
            debit += getTotalSectionTreasury(f24Metadata.getF24Standard());
        }
        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getInps() != null) {
            debit += getTotalSectionInps(f24Metadata.getF24Standard());
        }
        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getRegion() != null) {
            debit += getTotalSectionRegion(f24Metadata.getF24Standard());
        }
        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getLocalTax() != null) {
            debit += getTotalSectionLocalTax(f24Metadata.getF24Standard());
        }
        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getSocialSecurity() != null) {
            debit += getTotalSectionSocialSecurityINAIL(f24Metadata.getF24Standard());
        }
        if (f24Metadata.getF24Standard() != null && f24Metadata.getF24Standard().getSocialSecurity().getSocSecRecords() != null) {
            debit += getTotalSectionSocialSecurityRecords(f24Metadata.getF24Standard());
        }
        return debit / 100;
    }

    private double getTotalSectionSocialSecurityINAIL(F24Standard f24Standard) {
        int debit = 0;
        int credit = 0;
        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getRecords() != null) {
            for (int i = 0; i < f24Standard.getSocialSecurity().getRecords().size(); i++) {
                if (f24Standard.getSocialSecurity().getRecords().get(i).getDebit() != null) {
                    debit = debit + f24Standard.getSocialSecurity().getRecords().get(i).getDebit();
                }
                if (f24Standard.getSocialSecurity().getRecords().get(i).getCredit() != null) {
                    credit = credit + f24Standard.getSocialSecurity().getRecords().get(i).getCredit();
                }

            }
        }
        return debit - credit;
    }

    private double getTotalSectionSocialSecurityRecords(F24Standard f24Standard) {
        int debit = 0;
        int credit = 0;
        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getSocSecRecords() != null) {
            for (int i = 0; i < f24Standard.getSocialSecurity().getSocSecRecords().size(); i++) {
                if (f24Standard.getSocialSecurity().getSocSecRecords().get(i).getDebit() != null) {
                    debit = debit + f24Standard.getSocialSecurity().getSocSecRecords().get(i).getDebit();
                }
                if (f24Standard.getSocialSecurity().getSocSecRecords().get(i).getCredit() != null) {
                    credit = credit + f24Standard.getSocialSecurity().getSocSecRecords().get(i).getCredit();
                }
            }
        }
        return debit - credit;
    }

    private double getTotalSectionTreasury(F24Standard f24Standard) {
        int debit = 0;
        int credit = 0;
        if (f24Standard.getTreasury() != null && f24Standard.getTreasury().getRecords() != null) {
            for (int i = 0; i < f24Standard.getTreasury().getRecords().size(); i++) {
                if (f24Standard.getTreasury().getRecords().get(i).getDebit() != null) {
                    debit = debit + f24Standard.getTreasury().getRecords().get(i).getDebit();
                }
                if (f24Standard.getTreasury().getRecords().get(i).getCredit() != null) {
                    credit = credit + f24Standard.getTreasury().getRecords().get(i).getCredit();
                }
            }
        }
        return debit - credit;
    }

    private double getTotalSectionInps(F24Standard f24Standard) {
        int debit = 0;
        int credit = 0;
        if (f24Standard.getInps() != null && f24Standard.getInps().getRecords() != null) {
            for (int i = 0; i < f24Standard.getInps().getRecords().size(); i++) {
                if (f24Standard.getInps().getRecords().get(i).getDebit() != null) {
                    debit = debit + f24Standard.getInps().getRecords().get(i).getDebit();
                }
                if (f24Standard.getInps().getRecords().get(i).getCredit() != null) {
                    credit = credit + f24Standard.getInps().getRecords().get(i).getCredit();
                }

            }
        }
        return debit - credit;
    }

    private double getTotalSectionRegion(F24Standard f24Standard) {
        int debit = 0;
        int credit = 0;
        if (f24Standard.getRegion() != null && f24Standard.getRegion().getRecords() != null) {
            for (int i = 0; i < f24Standard.getRegion().getRecords().size(); i++) {
                if (f24Standard.getRegion().getRecords().get(i).getDebit() != null) {
                    debit = debit + f24Standard.getRegion().getRecords().get(i).getDebit();
                }
                if (f24Standard.getRegion().getRecords().get(i).getCredit() != null) {
                    credit = credit + f24Standard.getRegion().getRecords().get(i).getCredit();
                }
            }
        }
        return debit - credit;
    }

    private double getTotalSectionLocalTax(F24Standard f24Standard) {
        int debit = 0;
        int credit = 0;
        if (f24Standard.getLocalTax() != null && f24Standard.getLocalTax().getRecords() != null) {
            for (int i = 0; i < f24Standard.getLocalTax().getRecords().size(); i++) {
                if (f24Standard.getLocalTax().getRecords().get(i).getDebit() != null) {
                    debit = debit + f24Standard.getLocalTax().getRecords().get(i).getDebit();
                }
                if (f24Standard.getLocalTax().getRecords().get(i).getCredit() != null) {
                    credit = credit + f24Standard.getLocalTax().getRecords().get(i).getCredit();
                }
            }
        }
        return debit - credit;
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

    @Override
    public int getExpectedNumberOfPages(F24Metadata f24Metadata) {
        if(f24Metadata.getF24Standard() == null) {
            return 0;
        }

        int pages = 1;
        F24Standard f24Standard = f24Metadata.getF24Standard();
        if (f24Standard.getTreasury() != null && f24Standard.getTreasury().getRecords() != null) {
            int recordsNum = f24Standard.getTreasury().getRecords().size();
            pages = MetadataInspector.getTotalPagesNecessaryForSection(recordsNum, TAX_RECORDS_NUMBER.getRecordsNum(), pages);
        }

        if (f24Standard.getInps() != null && f24Standard.getInps().getRecords() != null) {
            int recordsNum = f24Standard.getInps().getRecords().size();
            pages = MetadataInspector.getTotalPagesNecessaryForSection(recordsNum, UNIV_RECORDS_NUMBER.getRecordsNum(), pages);
        }

        if (f24Standard.getRegion() != null && f24Standard.getRegion().getRecords() != null) {
            int recordsNum = f24Standard.getRegion().getRecords().size();
            pages = MetadataInspector.getTotalPagesNecessaryForSection(recordsNum, UNIV_RECORDS_NUMBER.getRecordsNum(), pages);
        }

        if (f24Standard.getLocalTax() != null && f24Standard.getLocalTax().getRecords() != null) {
            int recordsNum = f24Standard.getLocalTax().getRecords().size();
            pages = MetadataInspector.getTotalPagesNecessaryForSection(recordsNum, UNIV_RECORDS_NUMBER.getRecordsNum(), pages);
        }

        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getRecords() != null) {
            int recordsNum = f24Standard.getSocialSecurity().getRecords().size();
            pages = MetadataInspector.getTotalPagesNecessaryForSection(recordsNum, INAIL_RECORDS_NUMBER.getRecordsNum(), pages);
        }

        if (f24Standard.getSocialSecurity() != null && f24Standard.getSocialSecurity().getSocSecRecords() != null) {
            int recordsNum = f24Standard.getSocialSecurity().getSocSecRecords().size();
            pages = MetadataInspector.getTotalPagesNecessaryForSection(recordsNum, SOC_RECORDS_NUMBER.getRecordsNum(), pages);
        }

        return pages * STANDARD_DEFAULT_NUMBER_OF_COPIES;
    }
}
