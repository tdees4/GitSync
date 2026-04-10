package io.github.tdees15.gitsync.service;

import io.github.tdees15.gitsync.common.ActionType;
import io.github.tdees15.gitsync.common.WebhookEvent;
import io.github.tdees15.gitsync.model.Subscription;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

@Service
public class GithubWebhookService {

    private final WebhookFilterService webhookFilterService;

    public GithubWebhookService(WebhookFilterService webhookFilterService) {
        this.webhookFilterService = webhookFilterService;
    }

    public List<Subscription> filterSubscriptionsByEventAndBranch(List<Subscription> subscriptions,
                                                                  WebhookEvent event, String branchName) {
        return subscriptions.stream()
                .filter(subscription ->
                        subscription.getFilters().isEmpty() || subscription.getFilters().stream()
                        .anyMatch(filter -> webhookFilterService.isMatch(filter, event, branchName)))
                .toList();
    }

    public List<Subscription> filterSubscriptionsByEventAndBranchAndAction(List<Subscription> subscriptions, WebhookEvent event,
                                                                           String branchName, ActionType action) {
        return subscriptions.stream()
                .filter(subscription ->
                        subscription.getFilters().isEmpty() || subscription.getFilters().stream()
                        .anyMatch(filter -> webhookFilterService.isMatch(filter, event, branchName, action)))
                .toList();
    }

    public boolean isSignatureValid(String payload, String signatureHeader, String secret) {
        if (payload == null || signatureHeader == null || secret == null) {
            return false;
        }

        try {
            String hashType = "HmacSHA256";
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), hashType);
            Mac mac = Mac.getInstance(hashType);
            mac.init(keySpec);

            byte[] rawHash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = "sha256=" + HexFormat.of().formatHex(rawHash);

            return MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    signatureHeader.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            return false;
        }
    }

}
