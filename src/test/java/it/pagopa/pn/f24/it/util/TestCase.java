package it.pagopa.pn.f24.it.util;

import static it.pagopa.pn.f24.it.util.TestUtils.*;

public enum TestCase {
    METADATA_SIMPLIFIED_WITH_COST(METADATA_SIMPLIFIED_WITH_COST_FILEKEY, true),
    INVALID_METADATA_SIMPLIFIED_WITH_COST(INVALID_METADATA_SIMPLIFIED_WITH_COST_FILEKEY, true),
    METADATA_SIMPLIFIED_WITHOUT_COST(METADATA_SIMPLIFIED_WITHOUT_COST_FILEKEY, false);
    TestCase(String fileKey, boolean applyCost) {
        this.fileKey = fileKey;
        this.applyCost = applyCost;
    }
    private final String fileKey;
    private final boolean applyCost;

    public String getFileKey() { return this.fileKey; }

    public boolean getApplyCost() { return this.applyCost; }
}
