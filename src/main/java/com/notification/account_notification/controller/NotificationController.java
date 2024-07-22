package com.notification.account_notification.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.notification.account_notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notify")
    public String notify(@RequestBody JsonNode accountDetails) {
        notificationService.sendEmailNotification(accountDetails);
        return "Notification sent!";
    }
}