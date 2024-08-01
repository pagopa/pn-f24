package it.pagopa.pn.f24.middleware.eventbus.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.pn.api.dto.events.PnF24PdfSetReadyEvent;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;

import java.util.*;

import it.pagopa.pn.f24.dto.F24Request;

import org.junit.Test;

public class PnF24AsyncEventBuilderHelperTest {

    @Test
    public void testBuildMetadataValidationEndEvent() {
        F24MetadataValidationIssue f24MetadataValidationIssue = mock(F24MetadataValidationIssue.class);
        when(f24MetadataValidationIssue.getCode()).thenReturn("Code");
        when(f24MetadataValidationIssue.getDetail()).thenReturn("Detail");
        when(f24MetadataValidationIssue.getElement()).thenReturn("Element");

        ArrayList<F24MetadataValidationIssue> errors = new ArrayList<>();
        errors.add(f24MetadataValidationIssue);
        PnF24AsyncEventBuilderHelper.buildMetadataValidationEndEvent("42", "42", errors).getDetail();
        verify(f24MetadataValidationIssue).getCode();
        verify(f24MetadataValidationIssue).getDetail();
        verify(f24MetadataValidationIssue).getElement();
    }

    @Test
    public void testConvertErrors() {
        assertTrue(PnF24AsyncEventBuilderHelper.convertErrors(new ArrayList<>()).isEmpty());
        assertTrue(PnF24AsyncEventBuilderHelper.convertErrors(null).isEmpty());
    }

    @Test
    public void testBuildPdfSetReadyEventWithOrderedPathTokenList(){
        List<String> pathTokenList = getOrderedPathTokenList();

        PnF24PdfSetReadyEvent res = PnF24AsyncEventBuilderHelper.buildPdfSetReadyEvent(getF24Request());
        for (int i=0; i<pathTokenList.size(); i++){
            String token = res.getDetail().getPdfSetReady().getGeneratedPdfsUrls().get(i).getPathTokens();
            assertEquals(pathTokenList.get(i), token);
        }
    }

    private F24Request getF24Request(){
        F24Request request = new F24Request();

        Map<String, F24Request.FileRef> files = new HashMap<>();
        for(int i=0; i<50; i++)
            files.put("CACHE#ABC-1234#NO_COST#0_"+i, new F24Request.FileRef("PN-f24"));
        files.put("CACHE#ABC-1234#NO_COST#004", new F24Request.FileRef("PN-f24"));
        files.put("CACHE#ABC-1234#NO_COST#test", new F24Request.FileRef("PN-f24"));
        request.setFiles(files);

        return request;
    }

    private List<String> getOrderedPathTokenList() {
       return Arrays.asList("0_0", "0_1", "0_2", "0_3", "0_4", "0_5", "0_6", "0_7", "0_8", "0_9", "0_10", "0_11", "0_12", "0_13", "0_14", "0_15", "0_16", "0_17", "0_18", "0_19",
                "0_20", "0_21", "0_22", "0_23", "0_24", "0_25", "0_26", "0_27", "0_28", "0_29", "0_30", "0_31", "0_32", "0_33", "0_34", "0_35", "0_36", "0_37", "0_38", "0_39",
                "004", "0_40", "0_41", "0_42", "0_43", "0_44", "0_45", "0_46", "0_47", "0_48", "0_49", "test");
    }

}

