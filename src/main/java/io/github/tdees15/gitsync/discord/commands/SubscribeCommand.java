package io.github.tdees15.gitsync.discord.commands;

import io.github.tdees15.gitsync.model.Subscription;
import io.github.tdees15.gitsync.service.SubscriptionService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscribeCommand implements SlashCommand {

    private final SubscriptionService subscriptionService;

    public SubscribeCommand(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(
                OptionType.STRING,
                "repository",
                "The name of the repository you want to subscribe to (Ex: tdees4/repo)"
                ).setRequired(true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getMember() == null) {
            System.err.println("event.getMember() returns null...");
            return;
        }

        event.deferReply(true).queue();

        try {
            if (!event.getMember().hasPermission(event.getGuildChannel(), Permission.MANAGE_CHANNEL)) {
                event.getHook()
                        .setEphemeral(true)
                        .sendMessage("❌ You need `Manage Channels` permission to use this command!")
                        .queue();
                return;
            }

            if (event.getOption("repository") == null) {
                System.err.println("Must define repository option");
                return;
            }

            String repository = event.getOption("repository").getAsString();
            String guildId = event.getGuild().getId();
            String channelId = event.getChannelId();
            String userId = event.getUser().getId();

            Subscription subscription = subscriptionService.createSubscription(
                    guildId, channelId, repository, userId
            );

            event.getHook().sendMessage(
                    "✅ Successfully subscribed <#" + channelId + "> to **" + repository + "**\n" +
                            "📌 All events from all branches will be announced here.\n" +
                            "💡 Use `/add-filter` to customize what events you want to see.\n" +
                            "❓ Use `/help-webhook` to learn how to set up your repository for this bot!"
            ).queue();

        } catch (IllegalArgumentException e) {
            event.getHook()
                    .setEphemeral(true)
                    .sendMessage("❌ " + e.getMessage())
                    .queue();
        } catch (IllegalStateException e) {
            event.getHook()
                    .setEphemeral(true)
                    .sendMessage("⚠️ " + e.getMessage())
                    .queue();
        } catch (Exception e) {
            event.getHook()
                    .setEphemeral(true)
                    .sendMessage("❌ An error has occurred: " + e.getMessage())
                    .queue();
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Subscribe the current text channel to a repository.";
    }
}
