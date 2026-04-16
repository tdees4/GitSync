package io.github.tdees15.gitsync.handler;

import io.github.tdees15.gitsync.model.Subscription;
import io.github.tdees15.gitsync.service.DiscordEmbedService;
import io.github.tdees15.gitsync.service.GithubWebhookService;
import io.github.tdees15.gitsync.service.SubscriptionService;
import io.github.tdees15.gitsync.service.UserLinkService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IMentionable;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractGitHubWebhookHandler implements GitHubWebhookHandler {

    public record WebhookContext(
            String repoOwner,
            String repoName,
            String fullRepoName,
            String branchName,
            String assignedName,
            JsonNode payload
    ) {}

    protected final DiscordEmbedService discordEmbedService;
    protected final GithubWebhookService githubWebhookService;
    protected final SubscriptionService subscriptionService;
    protected final UserLinkService userLinkService;
    protected final JDA jda;

    public AbstractGitHubWebhookHandler(DiscordEmbedService discordEmbedService,
                                        GithubWebhookService githubWebhookService,
                                        SubscriptionService subscriptionService,
                                        UserLinkService userLinkService,
                                        JDA jda) {
        this.discordEmbedService = discordEmbedService;
        this.githubWebhookService = githubWebhookService;
        this.subscriptionService = subscriptionService;
        this.userLinkService = userLinkService;
        this.jda = jda;
    }

    @Override
    public void handle(JsonNode payload) {
        if (!shouldHandle(payload))
            return;

        String fullRepoName = payload.get("repository").get("full_name").asString();
        String[] repoSplit = fullRepoName.split("/");

        if (repoSplit.length < 2) {
            log.error("Repo name {} given in improper format", fullRepoName);
            return;
        }

        String repoOwner = repoSplit[0];
        String repoName = repoSplit[1];
        String branchName = payload.get("ref").asString().replace("refs/heads/", "");

        String gitHubUsername = getGitHubUsername(payload);

        List<Subscription> subscriptions = getSubscriptions(
                new WebhookContext(repoOwner, repoName, fullRepoName, branchName, "", payload)
        );

        for (Subscription subscription : subscriptions) {
            String assignedName = userLinkService.findByGithubUsername(gitHubUsername)
                            .flatMap(link -> Optional.ofNullable(jda.getGuildById(subscription.getGuildId()))
                                    .map(guild -> guild.getMemberById(link.getDiscordId())))
                            .map(IMentionable::getAsMention)
                            .orElse(gitHubUsername);

            WebhookContext context = new WebhookContext(repoOwner, repoName, fullRepoName, branchName, assignedName, payload);
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
