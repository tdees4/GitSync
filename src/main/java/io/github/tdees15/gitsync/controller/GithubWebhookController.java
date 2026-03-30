package io.github.tdees15.gitsync.controller;

import io.github.tdees15.gitsync.model.DiscordServerConfig;
import io.github.tdees15.gitsync.repository.DiscordServerConfigRepository;
import io.github.tdees15.gitsync.service.GithubWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/webhook/github/{webhookId}")
public class GithubWebhookController {

    private final GithubWebhookService githubWebhookService;
    private final DiscordServerConfigRepository discordServerConfigRepository;

    public GithubWebhookController(GithubWebhookService githubWebhookService,
                                   DiscordServerConfigRepository discordServerConfigRepository) {
        this.githubWebhookService = githubWebhookService;
        this.discordServerConfigRepository = discordServerConfigRepository;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @PathVariable String webhookId,
            @RequestHeader("X-Hub-Signature-256") String signatureHeader,
            @RequestBody String payload
    ) {
        DiscordServerConfig serverConfig = discordServerConfigRepository.findByWebhookId(webhookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Webhook ID not identified"));

        if (!githubWebhookService.isSignatureValid(payload, signatureHeader, serverConfig.getWebhookSecret()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // TODO: Complete post-security check logic

        return ResponseEntity.accepted().build();
    }

}
