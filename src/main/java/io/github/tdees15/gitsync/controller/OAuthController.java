package io.github.tdees15.gitsync.controller;

import io.github.tdees15.gitsync.github.dto.GitHubTokenResponse;
import io.github.tdees15.gitsync.github.dto.GitHubUser;
import io.github.tdees15.gitsync.service.GitHubOAuthService;
import io.github.tdees15.gitsync.service.LinkStateService;
import io.github.tdees15.gitsync.service.UserLinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handle authentication endpoints and orchestrates creation of user links in the database
 */
@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    private final LinkStateService linkStateService;
    private final GitHubOAuthService gitHubOAuthService;
    private final UserLinkService userLinkService;

    public OAuthController(LinkStateService linkStateService,
                           GitHubOAuthService gitHubOAuthService,
                           UserLinkService userLinkService) {
        this.linkStateService = linkStateService;
        this.gitHubOAuthService = gitHubOAuthService;
        this.userLinkService = userLinkService;
    }

    @GetMapping("/github/callback")
    public ResponseEntity<String> githubCallback(
            @RequestParam String code,
            @RequestParam String state) {

        String discordId = linkStateService.getDiscordId(state);

        if (discordId == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired link request");
        }

        GitHubTokenResponse tokenResponse = gitHubOAuthService.exchangeCodeForToken(code);

        GitHubUser githubUser = gitHubOAuthService.getGitHubUser(tokenResponse.getAccessToken());

        userLinkService.createLink(
                discordId,
                githubUser.getId(),
                githubUser.getLogin(),
                tokenResponse.getAccessToken()
        );

        linkStateService.deleteState(state);

        return ResponseEntity.ok(
                "<html><body><h1>Successfully linked!</h1>" +
                        "<p>You can close this window and return to Discord.</p></body></html>"
        );
    }

}
