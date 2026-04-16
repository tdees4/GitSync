package io.github.tdees15.gitsync.handler;

import io.github.tdees15.gitsync.common.ActionType;
import io.github.tdees15.gitsync.common.WebhookEvent;
import io.github.tdees15.gitsync.model.Subscription;
import io.github.tdees15.gitsync.service.DiscordEmbedService;
import io.github.tdees15.gitsync.service.GithubWebhookService;
import io.github.tdees15.gitsync.service.SubscriptionService;
import io.github.tdees15.gitsync.service.UserLinkService;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.awt.*;
import java.util.List;

@Component
public class IssuesEventHandler extends AbstractGitHubWebhookHandler {

    public IssuesEventHandler(DiscordEmbedService discordEmbedService,
                              GithubWebhookService githubWebhookService,
                              SubscriptionService subscriptionService,
                              UserLinkService userLinkService,
                              JDA jda) {
        super(discordEmbedService, githubWebhookService, subscriptionService, userLinkService, jda);
    }

    @Override
    protected boolean shouldHandle(JsonNode payload) {
        return ActionType.fromString(payload.get("action").asString()) != null;
    }

    @Override
    protected String getGitHubUsername(JsonNode payload) {
        return payload.get("sender").get("login").asString();
    }

    @Override
    protected List<Subscription> getSubscriptions(WebhookContext context) {
        ActionType action = ActionType.fromString(context.payload().get("action").asString());

        return githubWebhookService.filterSubscriptionsByEventAndBranchAndAction(
                subscriptionService.getSubscriptionsByRepositoryOwnerAndName(context.repoOwner(), context.repoName()),
                WebhookEvent.ISSUES,
                context.branchName(),
                action
        );
    }

    @Override
    protected void sendNotification(Subscription sub, WebhookContext context) {
        String action = context.payload().get("action").asString();
        int issueId = context.payload().get("issue").get("id").asInt();
        String issueUrl = context.payload().get("issue").get("url").asString();

        discordEmbedService.sendGitHubEmbed(
                sub.getChannelId(),
                "**" + context.fullRepoName() + "**: Issue",
                context.assignedName() + " has " + action + " issue " + issueId,
                issueUrl,
                Color.RED
        );
    }

    @Override
    public WebhookEvent getEvent() {
        return WebhookEvent.ISSUES;
    }

}
