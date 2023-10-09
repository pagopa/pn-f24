package it.pagopa.pn.f24.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SchedulerConfig {

    @Bean("f24Scheduler")
    public Scheduler scheduler(){
        return Schedulers.boundedElastic();
    }

}
