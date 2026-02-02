package io.github.tdees15.gitsync.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BotService {

    @Bean
    public JDA jda(@Value("${discord.bot.token}") String token,
                   List<ListenerAdapter> eventListeners) {
        JDABuilder builder = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);

        eventListeners.forEach(builder::addEventListeners);

        JDA jda = builder.build();

        jda.upsertCommand("ping", "PONG!").queue();

        return jda;
    }

}
