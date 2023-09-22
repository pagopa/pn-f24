package it.pagopa.pn.f24;


import it.pagopa.pn.f24.config.F24Config;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@Slf4j
@AllArgsConstructor
public class CorsGlobalConfiguration implements WebFluxConfigurer {

    private F24Config f24Config;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        if (log.isInfoEnabled())
            log.info("allowed domains:" + String.join(", ", f24Config.getCorsAllowedDomains()));

        corsRegistry.addMapping("/**")
                .allowedOrigins( f24Config.getCorsAllowedDomains().toArray( new String[0] ) )
                .allowedMethods("GET", "HEAD", "OPTIONS", "POST", "PUT", "DELETE", "PATCH")
                .maxAge(3600);
    }
}