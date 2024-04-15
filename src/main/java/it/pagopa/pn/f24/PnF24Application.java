package it.pagopa.pn.f24;

import it.pagopa.pn.commons.configs.listeners.TaskIdApplicationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class PnF24Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PnF24Application.class);
        app.addListeners(new TaskIdApplicationListener());
        app.run(args);
    }


}
