package io.github.tdees15.gitsync.service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;

import java.awt.*;

@Slf4j
@Service
public class DiscordEmbedService {

    private final JDA jda;

    public DiscordEmbedService(JDA jda) {
        this.jda = jda;
    }

    public void sendGitHubEmbed(String channelId, String title, String description, String url,
                                Color color, String[] author) {
        TextChannel channel = jda.getTextChannelById(channelId);

        log.info("JDA status: {}", jda.getStatus());
        log.info("Guild count: {}", jda.getGuilds().size());
        log.info("All text channels: {}", jda.getTextChannels());

        if (channel == null) {
            throw new IllegalArgumentException("Channel " + channelId + " not found");
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setUrl(url)
                .setColor(color)
                .setAuthor(author[0], author[1], author[2])
                .setFooter("Gitsync Bot", null)
                .build();

        channel.sendMessageEmbeds(embed).queue(
                success -> System.out.println("Discord accepted the embed!"),
                error -> {
                    System.err.println("Discord rejected the embed. Reason:");
                    error.printStackTrace();
                });
    }

}
