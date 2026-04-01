package io.github.tdees15.gitsync.controller;

import io.github.tdees15.gitsync.common.WebhookEvent;
import io.github.tdees15.gitsync.factory.GitHubWebhookHandlerFactory;
import io.github.tdees15.gitsync.handler.GitHubWebhookHandler;
import io.github.tdees15.gitsync.model.DiscordServerConfig;
import io.github.tdees15.gitsync.repository.DiscordServerConfigRepository;
import io.github.tdees15.gitsync.service.GithubWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/webhook/github/{webhookId}")
public class GitHubWebhookController {

    private final GithubWebhookService githubWebhookService;
    private final DiscordServerConfigRepository discordServerConfigRepository;
    private final GitHubWebhookHandlerFactory gitHubWebhookHandlerFactory;

    public GitHubWebhookController(GithubWebhookService githubWebhookService,
                                   DiscordServerConfigRepository discordServerConfigRepository,
                                   GitHubWebhookHandlerFactory gitHubWebhookHandlerFactory) {
        this.githubWebhookService = githubWebhookService;
        this.discordServerConfigRepository = discordServerConfigRepository;
        this.gitHubWebhookHandlerFactory = gitHubWebhookHandlerFactory;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @PathVariable String webhookId,
            @RequestHeader("X-Hub-Signature-256") String signatureHeader,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody String rawPayload
    ) {
        DiscordServerConfig serverConfig = discordServerConfigRepository.findByWebhookId(webhookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Webhook ID not identified"));

        if (!githubWebhookService.isSignatureValid(rawPayload, signatureHeader, serverConfig.getWebhookSecret()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode payload = mapper.readTree(rawPayload);

        GitHubWebhookHandler handler = null;
        try {
            handler = gitHubWebhookHandlerFactory.getHandler(WebhookEvent.fromString(eventType));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        if (handler == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        handler.handle(payload);

        return ResponseEntity.accepted().build();
    }

}
