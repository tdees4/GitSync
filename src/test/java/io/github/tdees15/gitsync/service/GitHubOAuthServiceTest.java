package io.github.tdees15.gitsync.service;

import io.github.tdees15.gitsync.config.GitHubApiProperties;
import io.github.tdees15.gitsync.config.OAuthProperties;
import io.github.tdees15.gitsync.github.dto.GitHubTokenResponse;
import io.github.tdees15.gitsync.github.dto.GitHubUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubOAuthServiceTest {

    @Mock
    OAuthProperties oAuthProperties;

    @Mock
    LinkStateService linkStateService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    GitHubApiProperties gitHubApiProperties;

    @InjectMocks
    GitHubOAuthService gitHubOAuthService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void generateAuthUrl_ValidInput_ReturnsProperUrl() {
        // Control state UUID
        UUID expectedUUID = UUID.randomUUID();

        try (MockedStatic<UUID> mockedUUID = mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(expectedUUID);

            String clientId = "geKEjEKW12hEo3";
            String callbackUrl = "http://localhost:8080/callback";

            when(oAuthProperties.getClientId()).thenReturn(clientId);
            when(oAuthProperties.getCallbackUrl()).thenReturn(callbackUrl);

            String discordId = "user123";

            String actualUrl = gitHubOAuthService.generateAuthorizationUrl(discordId);

            String expectedUrl = String.format(
                    gitHubApiProperties.getBasePublicUrl() + "/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=%s&scope=user:email",
                    clientId,
                    URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8),
                    expectedUUID
            );

            assertEquals(expectedUrl, actualUrl);
        }
    }

    @Test
    void exchangeCodeForToken_ValidInput_ReturnsValidTokenResponse() {
        String clientId = "bFEnnfeK123";
        String clientSecret = "FBEhJBFehjBFHJwJHDj";
        String code = "secret-code";
        String url = gitHubApiProperties.getBasePublicUrl() + "/login/oauth/access_token";

        GitHubTokenResponse expectedResponse = new GitHubTokenResponse();
        expectedResponse.setAccessToken("access-token");
        expectedResponse.setTokenType("access");
        expectedResponse.setScope("scope");

        when(oAuthProperties.getClientId()).thenReturn(clientId);
        when(oAuthProperties.getClientSecret()).thenReturn(clientSecret);

        when(restTemplate.postForObject(
                anyString(),
                any(),
                eq(GitHubTokenResponse.class)
        )).thenReturn(expectedResponse);

        gitHubOAuthService.exchangeCodeForToken(code);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<Map<String, String>>> entityCaptor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).postForObject(
                urlCaptor.capture(),
                entityCaptor.capture(),
                eq(GitHubTokenResponse.class)
        );

        assertEquals(url, urlCaptor.getValue());

        HttpEntity<Map<String, String>> capturedEntity = entityCaptor.getValue();
        assertNotNull(capturedEntity);

        assertEquals(
                "application/json",
                capturedEntity.getHeaders().getFirst("Accept")
        );

        Map<String, String> body = capturedEntity.getBody();

        assertNotNull(body);
        assertEquals(clientId, body.get("client_id"));
        assertEquals(clientSecret, body.get("client_secret"));
        assertEquals(code, body.get("code"));
    }

    @Test
    void getGitHubUser_ValidInput_ReturnsValidGitHubUser() {
        when(gitHubApiProperties.getBaseUrl())
                .thenReturn("https://api.github.com");

        GitHubUser mockUser = new GitHubUser();
        mockUser.setId("1");
        mockUser.setLogin("githubuser123");
        mockUser.setName("github user");
        mockUser.setEmail("githubuser@email.com");

        ResponseEntity<GitHubUser> mockResponse =
                ResponseEntity.ok(mockUser);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GitHubUser.class)
        )).thenReturn(mockResponse);

        GitHubUser result =
                gitHubOAuthService.getGitHubUser("123");

        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getEmail(), result.getEmail());
        assertEquals(mockUser.getLogin(), result.getLogin());
        assertEquals(mockUser.getName(), result.getName());

        ArgumentCaptor<String> urlCaptor =
                ArgumentCaptor.forClass(String.class);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<HttpEntity> entityCaptor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.GET),
                entityCaptor.capture(),
                eq(GitHubUser.class)
        );

        assertEquals(
                "https://api.github.com/user",
                urlCaptor.getValue()
        );

        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        assertNotNull(capturedEntity);

        HttpHeaders capturedHeaders = capturedEntity.getHeaders();

        String authHeader = capturedHeaders.getFirst("Authorization");
        assertEquals("Bearer 123", authHeader);
    }

}
