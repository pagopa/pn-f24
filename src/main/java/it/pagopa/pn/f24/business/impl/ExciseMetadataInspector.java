package it.pagopa.pn.f24.business.impl;

import it.pagopa.pn.f24.business.MetadataInspector;
import it.pagopa.pn.f24.dto.ApplyCostValidation;
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
        if (f24Excise.getTreasury() != null && f24Excise.getTreasury().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getTreasury().getRecords(), Tax::getApplyCost);
        }
        if (f24Excise.getInps() != null && f24Excise.getInps().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getInps().getRecords(), InpsRecord::getApplyCost);
        }
        if (f24Excise.getRegion() != null && f24Excise.getRegion().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getRegion().getRecords(), RegionRecord::getApplyCost);
        }
        if (f24Excise.getLocalTax() != null && f24Excise.getLocalTax().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getLocalTax().getRecords(), LocalTaxRecord::getApplyCost);
        }
        if (f24Excise.getExcise() != null && f24Excise.getExcise().getRecords() != null) {
            applyCostCounter += countElementsByPredicate(f24Excise.getExcise().getRecords(), ExciseTax::getApplyCost);
        }

        return applyCostCounter;
    }

    public ApplyCostValidation checkApplyCost(F24Metadata f24Metadata, boolean requiredApplyCost) {
        F24Excise f24Excise = f24Metadata.getF24Excise();
        if (f24Excise == null) {
            return requiredApplyCost ? ApplyCostValidation.REQUIRED_APPLY_COST_NOT_GIVEN : ApplyCostValidation.OK;
        }

        int validApplyCostFound = 0;
        int invalidApplyCostFound = 0;

        if (f24Excise.getTreasury() != null && f24Excise.getTreasury().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Excise.getTreasury().getRecords(), tax -> tax.getApplyCost() && (tax.getCredit() == null || tax.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Excise.getTreasury().getRecords(), tax -> tax.getApplyCost() && (tax.getCredit() != null && tax.getCredit() > 0));
        }
        if (f24Excise.getInps() != null && f24Excise.getInps().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Excise.getInps().getRecords(), inps -> inps.getApplyCost() && (inps.getCredit() == null || inps.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Excise.getInps().getRecords(), inps -> inps.getApplyCost() && (inps.getCredit() != null && inps.getCredit() > 0));
        }
        if (f24Excise.getRegion() != null && f24Excise.getRegion().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Excise.getRegion().getRecords(), region -> region.getApplyCost() && (region.getCredit() == null || region.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Excise.getRegion().getRecords(), region -> region.getApplyCost() && (region.getCredit() != null && region.getCredit() > 0));
        }
        if (f24Excise.getLocalTax() != null && f24Excise.getLocalTax().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Excise.getLocalTax().getRecords(), localTax -> localTax.getApplyCost() && (localTax.getCredit() == null || localTax.getCredit() == 0));

            invalidApplyCostFound += countElementsByPredicate(f24Excise.getLocalTax().getRecords(), localTax -> localTax.getApplyCost() && (localTax.getCredit() != null && localTax.getCredit() > 0));
        }
        if (f24Excise.getExcise() != null && f24Excise.getExcise().getRecords() != null) {
            validApplyCostFound += countElementsByPredicate(f24Excise.getExcise().getRecords(), ExciseTax::getApplyCost);
        }

        return MetadataInspector.verifyApplyCost(requiredApplyCost, validApplyCostFound, invalidApplyCostFound);
    }

    @Override
    public void addCostToDebit(F24Metadata f24Metadata, Integer cost) {
        if (f24Metadata.getF24Excise() != null) {
            F24Excise f24Excise = f24Metadata.getF24Excise();

            tryAddCostToTreasuryRecords(f24Excise, cost);

            tryAddCostToInpsRecords(f24Excise, cost);

            tryAddCostToRegionRecords(f24Excise, cost);

            tryAddCostToLocalTaxRecords(f24Excise, cost);

            tryAddCostToExciseRecords(f24Excise, cost);

        }
    }
    @Override
    public double getTotalAmount(F24Metadata f24Metadata) {
        double debit = 0;
        if (f24Metadata.getF24Excise() != null && f24Metadata.getF24Excise().getInps() != null)
            debit = getDebitInps(f24Metadata.getF24Excise());

        if (f24Metadata.getF24Excise() != null && f24Metadata.getF24Excise().getRegion() != null)
            debit = getDebitRegion(f24Metadata.getF24Excise());

        if (f24Metadata.getF24Excise() != null && f24Metadata.getF24Excise().getLocalTax() != null)
            debit = getDebitLocalTax(f24Metadata.getF24Excise());

        if (f24Metadata.getF24Excise() != null && f24Metadata.getF24Excise().getTreasury() != null)
            debit = getDebitTreasury(f24Metadata.getF24Excise());
        debit = debit / 100;
        return debit;
    }
    private double getDebitTreasury(F24Excise f24Excise) {
        int debit = 0;
        if (f24Excise.getTreasury() != null && f24Excise.getTreasury().getRecords() != null) {
            for (int i = 0; i < f24Excise.getTreasury().getRecords().size(); i++) {
                if (f24Excise.getTreasury().getRecords().get(i).getDebit() == null) {
                    debit = debit + f24Excise.getInps().getRecords().get(i).getDebit();
                }
            }
        }
        return debit;
    }

    private double getDebitInps(F24Excise f24Excise) {
        int debit = 0;
        if (f24Excise.getInps() != null && f24Excise.getInps().getRecords() != null) {
            for (int i = 0; i < f24Excise.getInps().getRecords().size(); i++) {
                debit = debit + f24Excise.getInps().getRecords().get(i).getDebit();
            }
        }
        return debit;
    }

    private double getDebitRegion(F24Excise f24Excise) {
        int debit = 0;
        if (f24Excise.getRegion() != null && f24Excise.getRegion().getRecords() != null) {
            for (int i = 0; i < f24Excise.getRegion().getRecords().size(); i++) {
                debit = debit + f24Excise.getRegion().getRecords().get(i).getDebit();
            }
        }
        return debit;
    }

    private double getDebitLocalTax(F24Excise f24Excise) {
        int debit = 0;
        if (f24Excise.getLocalTax() != null && f24Excise.getLocalTax().getRecords() != null) {
            for (int i = 0; i < f24Excise.getLocalTax().getRecords().size(); i++) {
                debit = debit + f24Excise.getLocalTax().getRecords().get(i).getDebit();
            }
        }
        return debit;
    }

    private void tryAddCostToTreasuryRecords(F24Excise f24Excise, Integer cost) {
        if (f24Excise.getTreasury() != null && f24Excise.getTreasury().getRecords() != null) {
            for (Tax tax : f24Excise.getTreasury().getRecords()) {
                if (tax.getApplyCost()) {
                    tax.setDebit(MetadataInspector.convertDebitSum(tax.getDebit(), cost));
                    return;
                }
            }
        }
    }

    private void tryAddCostToInpsRecords(F24Excise f24Excise, Integer cost) {
        if (f24Excise.getInps() != null && f24Excise.getInps().getRecords() != null) {
            for (InpsRecord inpsRecord : f24Excise.getInps().getRecords()) {
                if (inpsRecord.getApplyCost()) {
                    inpsRecord.setDebit(MetadataInspector.convertDebitSum(inpsRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }

    private void tryAddCostToRegionRecords(F24Excise f24Excise, Integer cost) {
        if (f24Excise.getRegion() != null && f24Excise.getRegion().getRecords() != null) {
            for (RegionRecord regionRecord : f24Excise.getRegion().getRecords()) {
                if (regionRecord.getApplyCost()) {
                    regionRecord.setDebit(MetadataInspector.convertDebitSum(regionRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }

    private void tryAddCostToLocalTaxRecords(F24Excise f24Excise, Integer cost) {
        if (f24Excise.getLocalTax() != null && f24Excise.getLocalTax().getRecords() != null) {
            for (LocalTaxRecord localTaxRecord : f24Excise.getLocalTax().getRecords()) {
                if (Boolean.TRUE.equals(localTaxRecord.getApplyCost())) {
                    localTaxRecord.setDebit(MetadataInspector.convertDebitSum(localTaxRecord.getDebit(), cost));
                    return;
                }
            }
        }
    }

    private void tryAddCostToExciseRecords(F24Excise f24Excise, Integer cost) {
        if (f24Excise.getExcise() != null && f24Excise.getExcise().getRecords() != null) {
            for (ExciseTax exciseTax : f24Excise.getExcise().getRecords()) {
                if (exciseTax.getApplyCost()) {
                    exciseTax.setDebit(MetadataInspector.convertDebitSum(exciseTax.getDebit(), cost));
                    return;
                }
            }
        }
    }
}

