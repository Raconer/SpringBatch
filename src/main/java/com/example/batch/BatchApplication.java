package com.example.batch;

import com.example.batch.config.BatchConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchApplication {
    private static final Logger log = LoggerFactory.getLogger(BatchApplication.class);

    public static void main(String[] args) throws Exception {
        log.info("@SpringBootApplication -> Start Main");
        System.exit(
            SpringApplication.exit(
                SpringApplication.run(BatchApplication.class, args)
            )
        );
        log.info("@SpringBootApplication -> End Main");
    }
}
