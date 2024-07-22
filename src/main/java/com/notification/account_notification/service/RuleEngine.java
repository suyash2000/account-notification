package com.notification.account_notification.service;

import org.mvel2.MVEL;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RuleEngine {

    public boolean validate(Map<String, Object> context, String validationRule) {
        return (boolean) MVEL.eval(validationRule, context);
    }

    public Map<String, Object> enrich(Map<String, Object> context, String enrichmentRule) {
        return (Map<String, Object>) MVEL.eval(enrichmentRule, context);
    }

    public Map<String, Object> transform(Map<String, Object> context, String transformationRule) {
        return (Map<String, Object>) MVEL.eval(transformationRule, context);
    }

    public String route(Map<String, Object> context, String routingRule) {
        return (String) MVEL.eval(routingRule, context);
    }
}
