package io.github.tdees15.gitsync.github.dto;

import lombok.Data;

@Data
public class GitHubUser {
    private String id;
    private String login;
    private String email;
    private String name;
}
