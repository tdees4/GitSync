package io.github.tdees15.gitsync.service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Arrays;

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
                success -> log.info("Discord accepted the embed!"),
                error -> {
                    log.error("Discord rejected the embed. Reason:");
                    log.error(Arrays.toString(error.getStackTrace()));
                });
    }

}
