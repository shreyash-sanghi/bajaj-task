package com.example.webookchallenge.runner;

import com.example.webookchallenge.service.WebhookChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupRunner.class);

    @Autowired
    private WebhookChallengeService webhookChallengeService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("SPRING BOOT APPLICATION STARTED SUCCESSFULLY!");
        logger.info("⚡ Triggering webhook challenge automatically...");

        // --------------- Execute the challenge ---------------
        webhookChallengeService.executeChallenge();
    }
}