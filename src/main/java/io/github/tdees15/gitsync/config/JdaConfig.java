package io.github.tdees15.gitsync.config;

import io.github.tdees15.gitsync.discord.commands.SlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!test")
@Slf4j
public class JdaConfig {

    @Value("${discord.bot.token}")
    private String token;

    @Bean
    public JDA jda(List<ListenerAdapter> eventListeners,
                   List<SlashCommand> commandList) throws InterruptedException {

        JDABuilder builder = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.MESSAGE_CONTENT)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL);

        eventListeners.forEach(builder::addEventListeners);

        builder.addEventListeners(new ListenerAdapter() {
            @Override
            public void onGuildReady(@NonNull GuildReadyEvent event) {
                log.info("Guild ready: {}", event.getGuild().getName());
            }

            @Override
            public void onReady(@NonNull ReadyEvent event) {
                log.info("Ready event fired. Guild count: {}", event.getGuildTotalCount());
            }
        });

        JDA jda = builder.build()
                .awaitReady();

        log.info("Logged in as: {}", jda.getSelfUser().getName());

        List<SlashCommandData> discordCommands = commandList.stream()
                .map(cmd -> Commands.slash(cmd.getName(), cmd.getDescription()).addOptions(cmd.getOptions()))
                .toList();

        jda.updateCommands().addCommands(discordCommands).queue();

        return jda;
    }

}
