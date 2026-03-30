package io.github.tdees15.gitsync.repository;

import io.github.tdees15.gitsync.model.DiscordServerConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscordServerConfigRepository extends JpaRepository<DiscordServerConfig, Long> {

    @NotNull
    Optional<DiscordServerConfig> findByGuildId(Long guildId);

    @NotNull
    Optional<DiscordServerConfig> findByWebhookId(String webhookId);

}
