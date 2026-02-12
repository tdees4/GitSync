package io.github.tdees15.gitsync.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class HelpWebhookCommand implements SlashCommand {

    @Override
    public String getName() {
        return "help-webhook";
    }

    @Override
    public String getDescription() {
        return "Shows instructions on how to set up a repository for GitSync";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(
                        """
                                :bulb: SETTING UP A WEBHOOK FOR GITSYNC :bulb:
                                
                                :one:  Go to your repository and click `Settings` :right_arrow: `Webhooks`
                                :two:  Click `Add webhook`
                                :three:  Copy the URL `https://bot.gitsync-bot.us/webhook/github` into the text box labeled `Payload URL`
                                :four:  **OPTIONAL** Enter a randomly generated secret string in the text box labeled `Secret`
                                :five:  Ensure SSL Verification is enabled
                                :six:  For simple setup, select `Send me everything` under `Which events would you like to trigger this webhook?`
                                :seven:  Click `Add webhook`
                                """
                )
                .setEphemeral(true)
                .queue();
    }

}
