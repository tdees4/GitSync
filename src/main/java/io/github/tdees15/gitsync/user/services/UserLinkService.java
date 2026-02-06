package io.github.tdees15.gitsync.user.services;

import io.github.tdees15.gitsync.user.entity.UserLink;
import io.github.tdees15.gitsync.user.repository.UserLinkRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserLinkService {

    private final UserLinkRepository userLinkRepository;

    public UserLinkService(UserLinkRepository userLinkRepository) {
        this.userLinkRepository = userLinkRepository;
    }

    @Transactional
    public UserLink createLink(String discordId, String githubId,
                               String githubUsername, String accessToken) {
        UserLink link = userLinkRepository.findByDiscordId(discordId)
                .orElse(new UserLink());

        link.setDiscordId(discordId);
        link.setGithubId(githubId);
        link.setGithubUsername(githubUsername);
        link.setLinkedAt(LocalDateTime.now());
        link.setAccessToken(accessToken);

        return userLinkRepository.save(link);
    }

    public Optional<UserLink> findByDiscordId(String discordId) {
        return userLinkRepository.findByDiscordId(discordId);
    }

    @Transactional
    public void deleteByDiscordId(String discordId) {
        userLinkRepository.deleteByDiscordId(discordId);
    }

}
