package io.github.tdees15.gitsync.handler;

import io.github.tdees15.gitsync.model.Subscription;
import io.github.tdees15.gitsync.service.DiscordEmbedService;
import io.github.tdees15.gitsync.service.GithubWebhookService;
import io.github.tdees15.gitsync.service.SubscriptionService;
import io.github.tdees15.gitsync.service.UserLinkService;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Slf4j
public abstract class AbstractGitHubWebhookHandler implements GitHubWebhookHandler {

    public record WebhookContext(
            String repoOwner,
            String repoName,
            String fullRepoName,
            String branchName,
            String discordMention,
            JsonNode payload
    ) {}

    protected final DiscordEmbedService discordEmbedService;
    protected final GithubWebhookService githubWebhookService;
    protected final SubscriptionService subscriptionService;
    protected final UserLinkService userLinkService;

    public AbstractGitHubWebhookHandler(DiscordEmbedService discordEmbedService,
                                        GithubWebhookService githubWebhookService,
                                        SubscriptionService subscriptionService,
                                        UserLinkService userLinkService) {
        this.discordEmbedService = discordEmbedService;
        this.githubWebhookService = githubWebhookService;
        this.subscriptionService = subscriptionService;
        this.userLinkService = userLinkService;
    }

    @Override
    public void handle(JsonNode payload) {
        if (!shouldHandle(payload))
            return;

        String fullRepoName = payload.get("repository").get("full_name").asString();
        String[] repoSplit = fullRepoName.split("/");

        if (repoSplit.length < 2) {
            log.error("Repo name {} given in improper format.", fullRepoName);
            return;
        }

        String repoOwner = repoSplit[0];
        String repoName = repoSplit[1];
        String branchName = payload.get("ref").asString().replace("refs/heads/", "");

        String gitHubUsername = getGitHubUsername(payload);
        String discordMention = userLinkService.findByGithubUsername(gitHubUsername)
                .map(link -> "<@" + link.getDiscordId() + ">")
                .orElse(gitHubUsername);

        WebhookContext context = new WebhookContext(repoOwner, repoName, fullRepoName, branchName, discordMention, payload);

        List<Subscription> subscriptions = getSubscriptions(context);

        for (Subscription subscription : subscriptions) {
            sendNotification(subscription, context);
        }
    }

    protected boolean shouldHandle(JsonNode payload) {
        return true;
    }

    protected abstract String getGitHubUsername(JsonNode payload);
    protected abstract List<Subscription> getSubscriptions(WebhookContext context);
    protected abstract void sendNotification(Subscription sub, WebhookContext context);
}
