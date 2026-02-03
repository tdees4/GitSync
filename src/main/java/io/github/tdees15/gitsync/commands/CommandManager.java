package io.github.tdees15.gitsync.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandManager extends ListenerAdapter {

    private final Map<String, SlashCommand> commands = new HashMap<>();

    public CommandManager(List<SlashCommand> commandList) {
        for (SlashCommand cmd : commandList) {
            commands.put(cmd.getName(), cmd);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommand command = commands.get(event.getName());

        if (command != null) {
            command.execute(event);
        }
    }

}
