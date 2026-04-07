package io.github.tdees15.gitsync.service;

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

    public boolean isMatchWithAction(@NonNull FilterConfig filter, WebhookEvent eventType,
                                     String branch, String action) {
        return matchesEvents(filter, eventType) && matchesActions(filter, action) && matchesBranch(filter, branch);
    }

    public boolean isMatch(@NonNull FilterConfig filter, WebhookEvent eventType, String branch) {
        return matchesEvents(filter, eventType) && matchesBranch(filter, branch);
    }

    private boolean matchesEvents(@NonNull FilterConfig filter, @NonNull WebhookEvent eventType) {
        List<WebhookEvent> acceptedEvents = filter.getEvents();

        for (WebhookEvent acceptedEvent : acceptedEvents) {
            if (acceptedEvent.equals(WebhookEvent.ALL) || acceptedEvent.equals(eventType))
                return true;
        }

        return false;
    }

    private boolean matchesActions(@NonNull FilterConfig filter, @NonNull String action) {
        List<String> acceptedActions = filter.getActions();

        for (String acceptedAction : acceptedActions) {
            if (acceptedAction.equals("*") || acceptedAction.equalsIgnoreCase(action))
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
