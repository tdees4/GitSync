package io.github.tdees15.gitsync.handler;

import io.github.tdees15.gitsync.common.WebhookEvent;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class PingEventHandler implements GitHubWebhookHandler {

    @Override
    public void handle(JsonNode payload) {
        String fullRepoName = payload.get("repository").asString();

        System.out.println("Received ping from " + fullRepoName);
    }

    @Override
    public WebhookEvent getEvent() {
        return WebhookEvent.PING;
    }

}
