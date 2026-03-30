package io.github.tdees15.gitsync.service;

import io.github.tdees15.gitsync.model.DiscordServerConfig;
import io.github.tdees15.gitsync.repository.DiscordServerConfigRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class DiscordServerConfigService {

    private final DiscordServerConfigRepository discordServerConfigRepository;

    public DiscordServerConfigService(DiscordServerConfigRepository discordServerConfigRepository) {
        this.discordServerConfigRepository = discordServerConfigRepository;
    }

    @NotNull
    @Transactional
    public DiscordServerConfig getServerConfig(Long guildId) {
        return discordServerConfigRepository.findByGuildId(guildId)
                .orElseGet(() -> discordServerConfigRepository.save(new DiscordServerConfig(guildId)));
    }

}
