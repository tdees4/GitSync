package io.github.tdees15.gitsync.model;

import io.github.tdees15.gitsync.common.WebhookEvent;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class FilterConfig {
    @Getter
    @Setter
    @Column(nullable = false)
    private String branchPattern; // Glob-based pattern

    @Column(name = "event_types")
    private String eventTypesStr; // "push,pull-request,etc..."

    @Column(name = "actions")
    private String actionsStr; // "opened,merged,etc..."

    public FilterConfig() {
        this("*", null, null);
    }

    public FilterConfig(String branchPattern, List<WebhookEvent> eventTypes, List<String> actions) {
        this.branchPattern = branchPattern;
        this.setEventTypes(eventTypes);
        this.setActions(actions);
    }

    @NonNull
    public List<WebhookEvent> getEvents() {
        return Arrays.stream(eventTypesStr.split(","))
                .map(WebhookEvent::fromString)
                .toList();
    }

    @NonNull
    public List<String> getActions() {
        return Arrays.asList(actionsStr.split(","));
    }

    public void setEventTypes(@Nullable List<WebhookEvent> events) {
        if (events == null || events.isEmpty()) {
            this.eventTypesStr = "*";
        } else {
            this.eventTypesStr = events.stream()
                    .map(WebhookEvent::toString)
                    .collect(Collectors.joining(","));
        }
    }

    public void setActions(@Nullable List<String> actions) {
        if (actions == null || actions.isEmpty()) {
            this.actionsStr = "*";
        } else {
            this.actionsStr = String.join(",", actions);
        }
    }
}
