package io.github.tdees15.gitsync.handler;

import io.github.tdees15.gitsync.common.WebhookEvent;
import io.github.tdees15.gitsync.model.Subscription;
import io.github.tdees15.gitsync.service.DiscordEmbedService;
import io.github.tdees15.gitsync.service.GithubWebhookService;
import io.github.tdees15.gitsync.service.SubscriptionService;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.awt.*;
import java.util.List;

@Component
public class PushEventHandler implements GitHubWebhookHandler {

    private final DiscordEmbedService discordEmbedService;
    private final GithubWebhookService githubWebhookService;
    private final SubscriptionService subscriptionService;

    public PushEventHandler(DiscordEmbedService discordEmbedService,
                            GithubWebhookService githubWebhookService,
                            SubscriptionService subscriptionService) {
        this.discordEmbedService = discordEmbedService;
        this.githubWebhookService = githubWebhookService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void handle(JsonNode payload) {
        String fullRepoName = payload.get("repository").get("full_name").asString();
        String[] repoSplit = fullRepoName.split("/");

        String repoOwner = repoSplit[0];
        String repoName = repoSplit[1];

        String username = payload.get("pusher").get("name").asString();

        int commitCount = payload.get("commits").size();
        String baseCommitUrl = payload.get("head_commit").requireNonNull().get("url").asString();


        String ref = payload.get("ref").asString();
        String branchName = ref.replace("refs/heads/", "");

        List<Subscription> subscriptions = githubWebhookService.filterSubscriptionsByEventAndBranch(
                        subscriptionService.getSubscriptionsByRepositoryOwnerAndName(repoOwner, repoName),
                        WebhookEvent.PUSH,
                        branchName
                );

        for (Subscription subscription : subscriptions) {
            discordEmbedService.sendGitHubEmbed(
                    subscription.getChannelId(),
                    "PUSH TO " + fullRepoName,
                    username + " has made a push of " + commitCount + " commit(s).",
                    baseCommitUrl,
                    Color.BLUE,
                    new String[]{username, null, null}
            );
        }
    }

    @Override
    public WebhookEvent getEvent() {
        return WebhookEvent.PUSH;
    }

}
