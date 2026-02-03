package io.github.tdees15.gitsync.oauth.services;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles temporary state storage.
 * State expires after a set amount of time.
 */
@Service
public class LinkStateService {

    private final Map<String, StateData> stateStore = new ConcurrentHashMap<>();

    /**
     * Saves state and user's Discord ID for {@code expiryMinutes} minutes
     *
     * @param state A unique string
     * @param discordId A user's Discord ID
     * @param expiryMinutes How long the state will be stored
     */
    public void saveState(String state, String discordId, int expiryMinutes) {
        StateData data = new StateData(discordId, LocalDateTime.now().plusMinutes(expiryMinutes));
        stateStore.put(state, data);
    }

    /**
     * If a given state is stored, retrieve the associated Discord ID. Otherwise,
     * return null
     *
     * @param state A unique state string
     * @return a user's Discord ID or null
     */
    @Nullable
    public String getDiscordId(String state) {
        StateData data = stateStore.get(state);

        if (data == null || data.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        return data.getDiscordId();
    }

    /**
     * If a given state exists in the state store, remove it.
     *
     * @param state The key of the state to remove
     */
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
