package com.example.webookchallenge.service;

import com.example.webookchallenge.model.WebhookRequest;
import com.example.webookchallenge.model.WebhookResponse;
import com.example.webookchallenge.model.SolutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookChallengeService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookChallengeService.class);

    @Autowired
    private RestTemplate restTemplate;

    // --------------- EXACT URLs ---------------
    private static final String WEBHOOK_GENERATION_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String WEBHOOK_SUBMISSION_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    public void executeChallenge() {
        logger.info("🚀 STARTING WEBHOOK CHALLENGE");

        try {
            logger.info("📡 Step 1: Sending POST request to generate webhook...");
            WebhookResponse webhookResponse = generateWebhook();

            if (webhookResponse != null && webhookResponse.getWebhook() != null) {
                logger.info("Webhook generation successful!");
                logger.info("Webhook URL: {}", webhookResponse.getWebhook());
                logger.info("Access Token: {}...",
                        webhookResponse.getAccessToken() != null ?
                                webhookResponse.getAccessToken().substring(0, Math.min(20, webhookResponse.getAccessToken().length())) :
                                "null");

                logger.info("Step 2: Solving SQL problem for regNo 187 (Odd)...");
                String finalSqlQuery = solveSqlProblem();
                logger.info("SQL solution generated successfully");
                logger.info("Step 3: Submitting solution to webhook URL...");
                submitSolution(webhookResponse, finalSqlQuery);

            } else {
                logger.error("Webhook generation failed - received null response");
            }

        } catch (Exception e) {
            logger.error("CHALLENGE EXECUTION FAILED: {}", e.getMessage(), e);
        }

        logger.info("WEBHOOK CHALLENGE COMPLETED");
    }

    private WebhookResponse generateWebhook() {
        try {
            logger.info("Sending POST request to: {}", WEBHOOK_GENERATION_URL);

            // Create request payload exactly as specified
            WebhookRequest request = new WebhookRequest("shreyash Jain", "187", "shreyash.code@gmail.com");
            logger.info("Request payload: name='{}', regNo='{}', email='{}'",
                    request.getName(), request.getRegNo(), request.getEmail());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

            logger.info("Making POST request...");
            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                    WEBHOOK_GENERATION_URL,
                    HttpMethod.POST,
                    entity,
                    WebhookResponse.class
            );

            logger.info("Response status: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful()) {
                WebhookResponse webhookResponse = response.getBody();
                if (webhookResponse != null) {
                    logger.info("Webhook generation successful!");
                    logger.info("Response body received with webhook and accessToken");
                    return webhookResponse;
                } else {
                    logger.error("Received null response body");
                    return null;
                }
            } else {
                logger.error("Webhook generation failed with status: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("Error during webhook generation: {}", e.getMessage(), e);
            return null;
        }
    }

    private String solveSqlProblem() {
        logger.info("Solving SQL Problem: Find highest salary NOT paid on 1st day of month");
        logger.info("Required output columns: SALARY, NAME, AGE, DEPARTMENT_NAME");

        // --------------- SQL Solution for Question 1 (Odd) ---------------

        String sqlQuery = """
            SELECT 
                p.AMOUNT as SALARY,
                CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) as NAME,
                FLOOR(DATEDIFF(CURDATE(), e.DOB) / 365.25) as AGE,
                d.DEPARTMENT_NAME
            FROM PAYMENTS p
            JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
            JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            WHERE DAY(p.PAYMENT_TIME) != 1
            ORDER BY p.AMOUNT DESC
            LIMIT 1
            """;

        logger.info("Final SQL query prepared for submission");

        return sqlQuery.trim();
    }

    private void submitSolution(WebhookResponse webhookResponse, String finalQuery) {
        try {
            logger.info("Submitting solution to webhook URL...");
            logger.info("Submission URL: {}", WEBHOOK_SUBMISSION_URL);

            SolutionRequest solutionRequest = new SolutionRequest(finalQuery);
            logger.info("Solution payload created with finalQuery");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", webhookResponse.getAccessToken()); // Using accessToken directly as specified

            webhookResponse.getAccessToken().substring(0, Math.min(20, webhookResponse.getAccessToken().length()));

            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);

            logger.info("Final SQL Query being submitted:");
            logger.info("Making POST request to submit solution...");

            ResponseEntity<String> response = restTemplate.exchange(
                    WEBHOOK_SUBMISSION_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            logger.info("Submission response status: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("SOLUTION SUBMITTED SUCCESSFULLY!");
                logger.info("📄 Response body: {}", response.getBody());
            } else {
                logger.error("Solution submission failed with status: {}", response.getStatusCode());
                logger.error("Response body: {}", response.getBody());
            }

        } catch (Exception e) {
            logger.error("Error during solution submission: {}", e.getMessage(), e);
        }
    }
}