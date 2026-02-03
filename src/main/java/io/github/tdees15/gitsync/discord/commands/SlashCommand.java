package io.github.tdees15.gitsync.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public interface SlashCommand {

    String getName();

    String getDescription();

    void execute(SlashCommandInteractionEvent event);

    default List<OptionData> getOptions() {
        return Collections.emptyList();
    }

}
