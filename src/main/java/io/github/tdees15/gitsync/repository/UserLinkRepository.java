package io.github.tdees15.gitsync.repository;

import io.github.tdees15.gitsync.model.UserLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLinkRepository extends JpaRepository<UserLink, Long> {
    Optional<UserLink> findByDiscordId(String discordId);
    void deleteByDiscordId(String discordId);
}
