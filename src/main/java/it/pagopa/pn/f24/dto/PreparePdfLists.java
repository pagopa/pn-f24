package it.pagopa.pn.f24.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PreparePdfLists {
    private F24Request f24Request;
    private List<F24File> filesToCreate = new ArrayList<>();
    private List<F24File> filesReady = new ArrayList<>();
    private List<F24File> filesNotReady = new ArrayList<>();

    public PreparePdfLists(F24Request f24Request) {
        this.f24Request = f24Request;
    }
}
