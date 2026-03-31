package io.github.tdees15.gitsync.handler;

import io.github.tdees15.gitsync.common.WebhookEvent;
import tools.jackson.databind.JsonNode;

public interface GitHubWebhookHandler {

    void handle(JsonNode payload);

    WebhookEvent getEvent();

}
