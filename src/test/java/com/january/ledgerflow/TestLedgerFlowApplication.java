package com.january.ledgerflow;

import org.springframework.boot.SpringApplication;

public class TestLedgerFlowApplication {

    public static void main(String[] args) {
        SpringApplication.from(LedgerFlowApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
