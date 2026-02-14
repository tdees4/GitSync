package io.github.tdees15.gitsync.service;

import io.github.tdees15.gitsync.model.UserLink;
import io.github.tdees15.gitsync.repository.UserLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserLinkServiceTest {

    @Mock
    private UserLinkRepository userLinkRepository;

    @InjectMocks
    private UserLinkService userLinkService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createLink_NewLink_ReturnsLink() {
        when(userLinkRepository.findByDiscordId(any()))
                .thenReturn(Optional.empty());

        when(userLinkRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        String discordId = "12394395";
        String githubId = "9248201";
        String githubUser = "coder123";
        String accessToken = "ndjsDnjndsJK";

        UserLink result = userLinkService.createLink(
                discordId,
                githubId,
                githubUser,
                accessToken
        );

        assertNotNull(result);
        assertEquals(discordId, result.getDiscordId());
        assertEquals(githubId, result.getGithubId());
        assertEquals(githubUser, result.getGithubUsername());
        assertEquals(accessToken, result.getAccessToken());
    }

    @Test
    void createLink_OverrideLink_ReturnsNewLink() {
        when(userLinkRepository.findByDiscordId(any()))
                .thenReturn(Optional.empty());

        when(userLinkRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        String discordId = "2737893";
        String initialGithubId = "2847291";
        String initialGithubUser = "coder123";
        String initialAccessToken = "jSDHuhdhksldSN";

        UserLink initial = userLinkService.createLink(
                discordId,
                initialGithubId,
                initialGithubUser,
                initialAccessToken
        );

        when(userLinkRepository.findByDiscordId(discordId))
                .thenReturn(Optional.of(initial));

        String newGithubId = "837219732";
        String newGithubUser = "coder321";
        String newAccessToken = "nsJDjJdsJ";

        UserLink result = userLinkService.createLink(
                discordId,
                newGithubId,
                newGithubUser,
                newAccessToken
        );

        assertNotNull(result);
        assertEquals(newGithubId, result.getGithubId());
        assertEquals(newGithubUser, result.getGithubUsername());
        assertEquals(newAccessToken, result.getAccessToken());
    }
}
