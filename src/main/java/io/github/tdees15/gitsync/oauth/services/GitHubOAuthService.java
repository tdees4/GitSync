package io.github.tdees15.gitsync.oauth.services;

import io.github.tdees15.gitsync.config.OAuthProperties;
import io.github.tdees15.gitsync.oauth.dto.GitHubTokenResponse;
import io.github.tdees15.gitsync.oauth.dto.GitHubUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
public class GitHubOAuthService {

    private final OAuthProperties oAuthProperties;
    private final LinkStateService linkStateService;
    private final RestTemplate restTemplate;

    public GitHubOAuthService(OAuthProperties oAuthProperties,
                              LinkStateService linkStateService) {
        this.oAuthProperties = oAuthProperties;
        this.linkStateService = linkStateService;
        this.restTemplate = new RestTemplate();
    }

    public String generateAuthorizationUrl(String discordId) {
        String state = UUID.randomUUID().toString();
        linkStateService.saveState(state, discordId, 10);

        return String.format(
                "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=%s&scope=user:email",
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
                "https://github.com/login/oauth/access_token",
                request,
                GitHubTokenResponse.class
        );
    }

    public GitHubUser getGitHubUser(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                request,
                GitHubUser.class
        ).getBody();
    }

}
