package it.pagopa.pn.f24.f24lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.f24.business.MetadataInspectorFactory;
import it.pagopa.pn.f24.config.F24Config;
import it.pagopa.pn.f24.f24lib.util.F24LibTestBuilder;
import it.pagopa.pn.f24.service.impl.F24GeneratorImpl;
import it.pagopa.pn.f24.service.impl.JsonServiceImpl;
import it.pagopa.pn.f24.service.impl.MetadataValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        JsonServiceImpl.class,
        LocalValidatorFactoryBean.class,
        ObjectMapper.class,
        MetadataValidatorImpl.class,
        F24LibTestBuilder.class,
        F24GeneratorImpl.class,
        MetadataInspectorFactory.class
})
public class F24LibIT {
    @MockBean
    F24Config f24Config;

    @Autowired
    F24LibTestBuilder f24LibTestBuilder;

    @BeforeEach
    void setupConfig() {
        Mockito.when( f24Config.getIsEnabledTaxCodeValidation() ).thenReturn (true);
    }

    @ParameterizedTest
    @CsvSource({
            "StandardWithApplyCost_VALID.json,false",
            "StandardWithApplyCost_VALID.json,false",
            "f24Elid_VALID.json,false",
            "f24Excise_VALID.json,false",
            "SimplifiedWithApplyCost.json,true",
            "SimplifiedWithApplyCost_INVALID-APPLY-COST.json,true",
            "METADATO-CORRETTO-ELID-MINIMAL.json,true",
            "METADATO-CORRETTO-EXCISE-MINIMAL.json,true",
            "METADATO-CORRETTO-SIMPL-MINIMAL.json,true",
            "METADATO-CORRETTO-STAND-MINIMAL.json,true",
            "SimplifiedWithoutApplyCost_INVALID.json,false"
    })
    public void libTestWithoutPdfParsing(String jsonFilePath, boolean shouldHaveApplyCost) {
        f24LibTestBuilder.execTest(jsonFilePath, shouldHaveApplyCost);
    }

    @ParameterizedTest
    @CsvSource({
            "f24Elid_VALID.json,false",
            "StandardWithApplyCost_VALID.json,false",
            "SimplifiedWithApplyCost.json,true",
            "SimplifiedPNFWithApplyCost.json,true"
    })
    public void libTestWithPdfParsing(String jsonFilePath, boolean shouldHaveApplyCost) {
        f24LibTestBuilder.execTest(jsonFilePath, shouldHaveApplyCost, true);
    }
}
