package io.github.tdees15.gitsync.factory;

import io.github.tdees15.gitsync.common.WebhookEvent;
import io.github.tdees15.gitsync.handler.GitHubWebhookHandler;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GitHubWebhookHandlerFactory {

    private final Map<WebhookEvent, GitHubWebhookHandler> handlerMap;

    public GitHubWebhookHandlerFactory(List<GitHubWebhookHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                    GitHubWebhookHandler::getEvent, handler -> handler
                ));
    }

    @Nullable
    public GitHubWebhookHandler getHandler(WebhookEvent type) {
        GitHubWebhookHandler handler = handlerMap.get(type);

        if (handler == null) {
            System.err.println("Illegal event type: " + type);
            return null;
        }

        return handler;
    }

}
