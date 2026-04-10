package io.github.tdees15.gitsync.model;

import io.github.tdees15.gitsync.common.ActionType;
import io.github.tdees15.gitsync.common.WebhookEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
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
        this("*", new ArrayList<>(), new ArrayList<>());
    }

    public FilterConfig(String branchPattern, List<WebhookEvent> eventTypes, List<ActionType> actions) {
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
    public List<ActionType> getActions() {
        return Arrays.stream(actionsStr.split(","))
                .map(ActionType::fromString)
                .toList();
    }

    public void setEventTypes(@NonNull List<WebhookEvent> events) {
        this.eventTypesStr = events.stream()
                .map(WebhookEvent::toString)
                .collect(Collectors.joining(","));
    }

    public void setActions(@NonNull List<ActionType> actions) {
        this.actionsStr = actions.stream()
                .map(ActionType::toString)
                .collect(Collectors.joining(","));
    }
}
