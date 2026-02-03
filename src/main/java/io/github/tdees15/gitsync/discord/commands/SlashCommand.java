package io.github.tdees15.gitsync.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

/**
 * The base Command class
 */
public interface SlashCommand {

    /**
     * Retrieves the name of the command
     *
     * @return the name of the command
     */
    String getName();

    /**
     * Retrieves the description of the command
     *
     * @return the description of the command
     */
    String getDescription();

    /**
     * Executes the command's function
     *
     * @param event The event object given by a listener
     */
    void execute(SlashCommandInteractionEvent event);

    /**
     * Retrieve the options of the command
     *
     * @return the list of command options
     */
    default List<OptionData> getOptions() {
        return Collections.emptyList();
    }

}
