package io.github.tdees15.gitsync.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

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

    public FilterConfig(String branchPattern, List<String> eventTypes, List<String> actions) {
        this.branchPattern = branchPattern;
        this.setEventTypes(eventTypes);
        this.setActions(actions);
    }

    public List<String> getEventTypes() {
        return Arrays.asList(eventTypesStr.split(","));
    }

    public List<String> getActions() {
        return Arrays.asList(actionsStr.split(","));
    }

    public void setEventTypes(@Nullable List<String> eventTypes) {
        if (eventTypes == null || eventTypes.isEmpty()) {
            this.eventTypesStr = "*";
        } else {
            this.eventTypesStr = String.join(",", eventTypes);
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
