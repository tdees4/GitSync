package io.github.tdees15.gitsync.service;

import io.github.tdees15.gitsync.config.GitHubApiProperties;
import io.github.tdees15.gitsync.config.OAuthProperties;
import io.github.tdees15.gitsync.github.dto.GitHubTokenResponse;
import io.github.tdees15.gitsync.github.dto.GitHubUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * Helper functions for GitHub authentication
 */
@Service
public class GitHubOAuthService {

    private final OAuthProperties oAuthProperties;
    private final LinkStateService linkStateService;
    private final RestTemplate restTemplate;
    private final GitHubApiProperties gitHubApiProperties;

    public GitHubOAuthService(OAuthProperties oAuthProperties,
                              LinkStateService linkStateService,
                              RestTemplate restTemplate,
                              GitHubApiProperties gitHubApiProperties) {
        this.oAuthProperties = oAuthProperties;
        this.linkStateService = linkStateService;
        this.restTemplate = restTemplate;
        this.gitHubApiProperties = gitHubApiProperties;
    }

    public String generateAuthorizationUrl(String discordId) {
        String state = UUID.randomUUID().toString();
        linkStateService.saveState(state, discordId, 10);

        return String.format(
                gitHubApiProperties.getBasePublicUrl() +
                        "/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=%s&scope=user:email",
                oAuthProperties.getClientId(),
                URLEncoder.encode(oAuthProperties.getCallbackUrl(), StandardCharsets.UTF_8),
                state
        );
    }

    public GitHubTokenResponse exchangeCodeForToken(String code) {
        Map<String, String> body = Map.of(
                "client_id", oAuthProperties.getClientId(),
                "client_secret", oAuthProperties.getClientSecret(),
                "code", code
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(
                gitHubApiProperties.getBasePublicUrl() + "/login/oauth/access_token",
                request,
                GitHubTokenResponse.class
        );
    }

    public GitHubUser getGitHubUser(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                gitHubApiProperties.getBaseUrl() + "/user",
                HttpMethod.GET,
                request,
                GitHubUser.class
        ).getBody();
    }

}
