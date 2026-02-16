package io.github.tdees15.gitsync.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import io.github.tdees15.gitsync.config.OAuthProperties;
import io.github.tdees15.gitsync.github.dto.GitHubTokenResponse;
import io.github.tdees15.gitsync.github.dto.GitHubUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@EnableWireMock
@ExtendWith(MockitoExtension.class)
public class GitHubOAuthServiceTest {

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Mock
    OAuthProperties oAuthProperties;

    @Mock
    LinkStateService linkStateService;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    GitHubOAuthService gitHubOAuthService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("https://api.github.com", wireMockExtension::baseUrl);
    }

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
                    "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=%s&scope=user:email",
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
        String url = "https://github.com/login/oauth/access_token";

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
        String accessToken = "token";
        String mockLogin = "\"githubuser123\"";
        int mockId = 1;
        String mockEmail = "\"githubuser@email.com\"";
        String mockName = "\"github user\"";

        wireMockExtension.stubFor(
                WireMock
                        .get(urlEqualTo("/user"))
                        .withHeader("Authorization", equalTo("Bearer " + accessToken))
                        .willReturn(jsonResponse(
                                "{\"login\": " + mockLogin + "," +
                                        "\"id\": " + mockId + "," +
                                        "\"email\":  " + mockEmail + "," +
                                        "\"name\":  " + mockName + "}",
                                200
                        ))
        );

        GitHubUser actualUser = gitHubOAuthService.getGitHubUser(accessToken);

        try {
            assertEquals(mockId, Integer.parseInt(actualUser.getId()));
        } catch (NumberFormatException e) {
            System.err.println("ID returned non-number value");
        }
        assertEquals(mockLogin.replaceAll("\"", ""), actualUser.getLogin());
        assertEquals(mockName.replaceAll("\"", ""), actualUser.getName());
        assertEquals(mockEmail.replaceAll("\"", ""), actualUser.getEmail());
    }

}
