package io.github.tdees15.gitsync.handler;

import io.github.tdees15.gitsync.common.WebhookEvent;
import io.github.tdees15.gitsync.model.Subscription;
import io.github.tdees15.gitsync.service.DiscordEmbedService;
import io.github.tdees15.gitsync.service.GithubWebhookService;
import io.github.tdees15.gitsync.service.SubscriptionService;
import io.github.tdees15.gitsync.service.UserLinkService;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.awt.*;
import java.util.List;

@Component
public class PushEventHandler extends AbstractGitHubWebhookHandler {

    public PushEventHandler(DiscordEmbedService discordEmbedService,
                            GithubWebhookService githubWebhookService,
                            SubscriptionService subscriptionService,
                            UserLinkService userLinkService) {
        super(discordEmbedService, githubWebhookService, subscriptionService, userLinkService);
    }

    @Override
    protected String getGitHubUsername(JsonNode payload) {
        return payload.path("pusher").path("name").asString();
    }

    @Override
    protected List<Subscription> getSubscriptions(WebhookContext context) {
        return githubWebhookService.filterSubscriptionsByEventAndBranch(
                subscriptionService.getSubscriptionsByRepositoryOwnerAndName(context.repoOwner(), context.repoName()),
                WebhookEvent.PUSH,
                context.branchName()
        );
    }

    @Override
    protected void sendNotification(Subscription sub, WebhookContext context) {
        int commitCount = context.payload().path("commits").size();

        String baseCommitMessage = context.payload().path("head_commit").requireNonNull().path("message").asString();
        if (baseCommitMessage.isEmpty())
            baseCommitMessage = "! No commit message found !";

        String baseCommitUrl = context.payload().path("head_commit").requireNonNull().path("url").asString();

        String[] author = {context.discordMention(), null, null};

        discordEmbedService.sendGitHubEmbed(
                sub.getChannelId(),
                "**" + context.fullRepoName() + "**: Push",
                "**" + context.discordMention() + "** has made a push of " + commitCount + " commit(s).\n\nHead commit message:\n" + baseCommitMessage,
                baseCommitUrl,
                Color.BLUE,
                author
        );
    }

    @Override
    public WebhookEvent getEvent() {
        return WebhookEvent.PUSH;
    }

}
