package com.microservices.demo.twitter.to.kafka.service;

import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private final StreamRunner streamRunner;

    public TwitterToKafkaServiceApplication(StreamRunner streamRunner) {
        this.streamRunner = streamRunner;
    }


    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("App starts ....");
        streamRunner.start();
    }
}
