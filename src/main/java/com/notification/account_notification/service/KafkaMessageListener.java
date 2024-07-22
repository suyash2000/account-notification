package com.notification.account_notification.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class KafkaMessageListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RuleEngine ruleEngine;

    @KafkaListener(topics = "account.details", groupId = "notification_group")
    public void listen(String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> accountDetailsMap = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});

        String validationRule = (String) redisTemplate.opsForValue().get("validationRule");
        String enrichmentRule = (String) redisTemplate.opsForValue().get("enrichmentRule");
        String transformationRule = (String) redisTemplate.opsForValue().get("transformationRule");
        String routingRule = (String) redisTemplate.opsForValue().get("routingRule");

        if (!ruleEngine.validate(accountDetailsMap, validationRule)) {
            return;
        }

        accountDetailsMap = ruleEngine.enrich(accountDetailsMap, enrichmentRule);
        accountDetailsMap = ruleEngine.transform(accountDetailsMap, transformationRule);
        String email = ruleEngine.route(accountDetailsMap, routingRule);
        String subject = ruleEngine.route(accountDetailsMap, routingRule);

        // Construct the JSON payload for the email notification
        JsonNode accountDetailsJson = objectMapper.valueToTree(accountDetailsMap);
        ((ObjectNode) accountDetailsJson).put("email", email);
        ((ObjectNode) accountDetailsJson).put("subject", subject);
        ((ObjectNode) accountDetailsJson).put("message", accountDetailsMap.toString());

        notificationService.sendEmailNotification(accountDetailsJson);
    }
}
