package com.chargebacks.processor.config;

import com.chargebacks.processor.command.ChargebackCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine;

@Configuration
public class PicocliConfig {

    @Bean
    public CommandLineRunner commandLineRunner(ChargebackCommand chargebackCommand) {
        return args -> {
            int exitCode = new CommandLine(chargebackCommand).execute(args);
            System.exit(exitCode);
        };
    }
}

