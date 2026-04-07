package io.github.tdees15.gitsync.repository;

import io.github.tdees15.gitsync.model.UserLink;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLinkRepository extends JpaRepository<UserLink, Long> {
    @NonNull
    Optional<UserLink> findByDiscordId(String discordId);

    void deleteByDiscordId(@NonNull String discordId);

    @NonNull
    Optional<UserLink> findByGithubId(String githubId);

    @NonNull
    Optional<UserLink> findByGithubUsername(String githubUsername);
}
