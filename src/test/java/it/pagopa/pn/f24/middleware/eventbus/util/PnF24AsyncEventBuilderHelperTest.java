package it.pagopa.pn.f24.middleware.eventbus.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.core.util.COWArrayList;
import it.pagopa.pn.api.dto.events.PnF24MetadataValidationIssue;
import it.pagopa.pn.f24.dto.F24MetadataValidationIssue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

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

}

