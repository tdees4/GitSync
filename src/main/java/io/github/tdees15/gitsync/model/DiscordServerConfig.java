package io.github.tdees15.gitsync.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Entity
@Table(name = "discord_server_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DiscordServerConfig {
    @Id
    private Long guildId;

    @Column(unique = true, nullable = false)
    private String webhookId;

    @Column(nullable = false)
    private String webhookSecret;

    public DiscordServerConfig(Long guildId) {
        this.guildId = guildId;
    }

    @PrePersist
    protected void onCreate() {
        if (this.webhookId == null) {
            this.webhookId = UUID.randomUUID().toString();
        }

        if (this.webhookSecret == null) {
            this.webhookSecret = generateSafeToken();
        }
    }

    private String generateSafeToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscordServerConfig that)) return false;
        return guildId != null && guildId.equals(that.getGuildId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
