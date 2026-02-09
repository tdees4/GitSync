package io.github.tdees15.gitsync.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Embeddable
public class FilterConfig {
    @Getter
    @Setter
    @Column(nullable = false)
    private String branchPattern; // Glob-based pattern

    @Getter
    @Setter
    @ElementCollection
    @CollectionTable(name = "filter_event_types")
    private List<String> eventTypes; // ["push", "pull_request", "issue", etc..]

    @Getter
    @Setter
    @ElementCollection
    @CollectionTable(name = "filter_actions")
    private List<String> actions; // ["opened", "merged", "closed", etc...]

    public FilterConfig() {
        this("*", null, null);
    }

    public FilterConfig(String branchPattern, List<String> eventTypes, List<String> actions) {
        this.branchPattern = branchPattern;
        this.eventTypes = eventTypes != null ? eventTypes : List.of("*");
        this.actions = actions != null ? actions : List.of("*");
    }
}
