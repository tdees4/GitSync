package io.github.tdees15.gitsync.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
                ":bulb: SETTING UP A WEBHOOK FOR GITSYNC :bulb:\n\n" +
                        ":one: Go to your repository and click Settings :right_arrow: Webhooks\n" +
                        ":two: Click \"Add Webhook\"\n" +
                        ":three: Copy the url \"https://bot.gitsync-bot.us/webhook/github\" into the text box labeled \"Payload URL\"\n" +
                        "**OPTIONAL** Enter a randomly generated secret string in the text box labeled \"Secret\"\n" +
                        ":four: For simple setup, select \"Send me everything\" under \"Which events would you like to trigger this webhook?\"\n" +
                        ":five: Click \"Add webhook\""
                )
                .setEphemeral(true)
                .queue();
    }

}
