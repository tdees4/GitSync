package io.github.tdees15.gitsync.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

/**
 * A command that allows users to link their GitHub account to their Discord account
 */
@Component
public class LinkCommand implements SlashCommand {

    private final String name = "link";
    private final String description = "Link your GitHub account to your Discord account.";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Hello!").queue();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
