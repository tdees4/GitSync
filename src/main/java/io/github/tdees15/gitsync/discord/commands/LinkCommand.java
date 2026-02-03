package io.github.tdees15.gitsync.discord.commands;

import io.github.tdees15.gitsync.oauth.services.GitHubOAuthService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

/**
 * A command that allows users to link their GitHub account to their Discord account
 */
@Component
public class LinkCommand implements SlashCommand {

    private final GitHubOAuthService gitHubOAuthService;

    public LinkCommand(GitHubOAuthService gitHubOAuthService) {
        this.gitHubOAuthService = gitHubOAuthService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String discordId = event.getUser().getId();

        String authUrl = gitHubOAuthService.generateAuthorizationUrl(discordId);

        event.reply("Click here to link your github account: " + authUrl)
                .setEphemeral(true)
                .queue();
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getDescription() {
        return "Link your GitHub account to your Discord account.";
    }

}
