package io.github.tdees15.gitsync.oauth.dto;

import lombok.Data;

@Data
public class GitHubUser {
    private String id;
    private String login;
    private String email;
    private String name;
}
