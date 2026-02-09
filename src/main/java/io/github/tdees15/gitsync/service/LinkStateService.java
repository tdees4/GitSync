package io.github.tdees15.gitsync.service;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles temporary state storage; State expires after a set amount of time
 */
@Service
public class LinkStateService {

    private final Map<String, StateData> stateStore = new ConcurrentHashMap<>();

    public void saveState(String state, String discordId, int expiryMinutes) {
        StateData data = new StateData(discordId, LocalDateTime.now().plusMinutes(expiryMinutes));
        stateStore.put(state, data);
    }

    @Nullable
    public String getDiscordId(String state) {
        StateData data = stateStore.get(state);

        if (data == null || data.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        return data.getDiscordId();
    }

    public void deleteState(String state) {
        stateStore.remove(state);
    }

    @Data
    @AllArgsConstructor
    private static class StateData {
        private String discordId;
        private LocalDateTime expiresAt;
    }

}
