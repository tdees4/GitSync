package io.github.tdees15.gitsync.service;

import io.github.tdees15.gitsync.common.ActionType;
import io.github.tdees15.gitsync.common.WebhookEvent;
import io.github.tdees15.gitsync.model.FilterConfig;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

@Service
public class WebhookFilterService {

    public boolean isMatch(@NonNull FilterConfig filter, WebhookEvent event,
                                     String branch, ActionType action) {
        return matchesEvents(filter, event) && matchesAction(filter, action) && matchesBranch(filter, branch);
    }

    public boolean isMatch(@NonNull FilterConfig filter, WebhookEvent event, String branch) {
        return matchesEvents(filter, event) && matchesBranch(filter, branch);
    }

    public boolean isMatch(@NonNull FilterConfig filter, WebhookEvent event, ActionType action) {
        return matchesEvents(filter, event) && matchesAction(filter, action);
    }

    private boolean matchesEvents(@NonNull FilterConfig filter, @NonNull WebhookEvent eventType) {
        List<WebhookEvent> acceptedEvents = filter.getEvents();

        if (acceptedEvents.isEmpty()) // == all events are valid
            return true;

        for (WebhookEvent acceptedEvent : acceptedEvents) {
            if (acceptedEvent.equals(eventType))
                return true;
        }

        return false;
    }

    private boolean matchesAction(@NonNull FilterConfig filter, @NonNull ActionType action) {
        List<ActionType> acceptedActions = filter.getActions();

        if (acceptedActions.isEmpty()) // == all actions are valid
            return true;

        for (ActionType acceptedAction : acceptedActions) {
            if (acceptedAction == action)
                return true;
        }
        return false;
    }

    private boolean matchesBranch(@NonNull FilterConfig filter, @NonNull String branchName) {
        String branchPattern = filter.getBranchPattern();

        if (!branchPattern.startsWith("glob:") && !branchPattern.startsWith("regex:"))
            branchPattern = "glob:" + branchPattern;

        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(branchPattern);
        return pathMatcher.matches(Path.of(branchName));
    }

}
