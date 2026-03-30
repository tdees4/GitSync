package io.github.tdees15.gitsync.discord.commands;

import io.github.tdees15.gitsync.model.DiscordServerConfig;
import io.github.tdees15.gitsync.service.DiscordServerConfigService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class WebhookSetupCommand implements SlashCommand {

    private final DiscordServerConfigService discordServerConfigService;

    public WebhookSetupCommand(DiscordServerConfigService discordServerConfigService) {
        this.discordServerConfigService = discordServerConfigService;
    }

    @Override
    public String getName() {
        return "webhook-setup";
    }

    @Override
    public String getDescription() {
        return "Shows instructions on how to set up a repository for GitSync";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null)
            return;

        Long guildId = event.getGuild().getIdLong();
        DiscordServerConfig serverConfig = discordServerConfigService.getServerConfig(guildId);

        event.reply(
                        """
                                :bulb: SETTING UP A WEBHOOK FOR GITSYNC :bulb:
                                
                                :one:  Go to your repository and click `Settings` :right_arrow: `Webhooks`
                                :two:  Click `Add webhook`
                                :three:  Copy the URL `https://bot.gitsync-bot.us/webhook/github/%s` into the text box labeled `Payload URL`
                                :four:  Enter the string `%s` into the text box labeled `Secret`
                                :five:  Ensure SSL Verification is enabled
                                :six:  Select `Send me everything` under `Which events would you like to trigger this webhook?`
                                :seven:  Click `Add webhook`
                                """.formatted(serverConfig.getWebhookId(), serverConfig.getWebhookSecret())
                )
                .setEphemeral(true)
                .queue();
    }

}
