package it.pagopa.pn.f24.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PreparePdfLists {
    private F24Request f24Request;
    private List<F24FileToCreate> filesToCreate = new ArrayList<>();
    private List<F24File> filesReady = new ArrayList<>();
    private List<F24File> filesNotReady = new ArrayList<>();

    public PreparePdfLists(F24Request f24Request) {
        this.f24Request = f24Request;
    }

    @Data
    public static class F24FileToCreate {
        private F24File file;
        private String metadataFileKey;

        public F24FileToCreate(F24File file, String metadataFileKey) {
            this.file = file;
            this.metadataFileKey = metadataFileKey;
        }
    }
}
