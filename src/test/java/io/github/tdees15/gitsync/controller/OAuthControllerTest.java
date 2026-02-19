package io.github.tdees15.gitsync.controller;

import io.github.tdees15.gitsync.github.dto.GitHubTokenResponse;
import io.github.tdees15.gitsync.github.dto.GitHubUser;
import io.github.tdees15.gitsync.service.GitHubOAuthService;
import io.github.tdees15.gitsync.service.LinkStateService;
import io.github.tdees15.gitsync.service.UserLinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuthControllerTest {

    @Mock
    LinkStateService linkStateService;

    @Mock
    GitHubOAuthService gitHubOAuthService;

    @Mock
    UserLinkService userLinkService;

    @InjectMocks
    OAuthController oAuthController;

    @BeforeEach
    void setUp() {

    }

    @Test
    void callback_ValidInputs_ReturnsOkResponse() {
        when(linkStateService.getDiscordId(any()))
                .thenReturn("user123");

        GitHubTokenResponse mockTokenResponse = new GitHubTokenResponse();
        mockTokenResponse.setAccessToken("secret-token");

        when(gitHubOAuthService.exchangeCodeForToken(any()))
                .thenReturn(mockTokenResponse);

        GitHubUser mockUser = new GitHubUser();
        mockUser.setId("user123");
        mockUser.setLogin("user");
        mockUser.setName("user");
        mockUser.setEmail("user@email.com");

        when(gitHubOAuthService.getGitHubUser(any()))
                .thenReturn(mockUser);

        ResponseEntity<?> response = oAuthController
                .githubCallback("123", "hrhertg");

        verify(linkStateService, times(1))
                .deleteState(any());

        verify(userLinkService, times(1))
                .createLink(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString()
                );

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void callback_InvalidOrExpiredState_Returns4xxResponse() {
        when(linkStateService.getDiscordId(any()))
                .thenReturn(null);

        ResponseEntity<?> response = oAuthController
                .githubCallback("123", "huiewufewu");

        assertTrue(response.getStatusCode().is4xxClientError());
    }

}
