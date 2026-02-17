package io.github.tdees15.gitsync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "github.api")
public class GitHubApiProperties {
    private String baseUrl;
    private String basePublicUrl;
}
