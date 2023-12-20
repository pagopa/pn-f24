package it.pagopa.pn.f24.f24lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.f24.business.MetadataInspectorFactory;
import it.pagopa.pn.f24.f24lib.util.F24LibTestBuilder;
import it.pagopa.pn.f24.service.impl.F24GeneratorImpl;
import it.pagopa.pn.f24.service.impl.JsonServiceImpl;
import it.pagopa.pn.f24.service.impl.MetadataValidatorImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    F24LibTestBuilder f24LibTestBuilder;
    @ParameterizedTest
    @CsvSource({
            //"SimplifiedWithApplyCost_VALID.json,false",
            "StandardWithApplyCost_VALID.json,false",
            //"f24Elid_VALID.json,false",
            //"f24Excise_VALID.json,false",
    })
    public void libTest(String jsonFilePath, boolean shouldHaveApplyCost) {
        f24LibTestBuilder.execTest(jsonFilePath, shouldHaveApplyCost);
    }
}
