package io.github.tdees15.gitsync.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class LinkStateServiceTest {

    @InjectMocks
    private LinkStateService linkStateService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getDiscordId_InvalidState_ReturnsNull() {
        String invalidState = "random-state";

        assertNull(linkStateService.getDiscordId(invalidState));
    }

    @Test
    void getDiscordId_ExpiredState_ReturnsNull() {
        String state = "123456789";
        String discordId = "user123";

        linkStateService.saveState(state, discordId, -1);

        String savedDiscordId = linkStateService.getDiscordId(state);

        assertNull(savedDiscordId);
    }

    @Test
    void getDiscordId_ValidState_ReturnsDiscordId() {
        String state = "123456789";
        String discordId = "user123";

        linkStateService.saveState(state, discordId, 1);

        String savedDiscordId = linkStateService.getDiscordId(state);

        assertEquals(discordId, savedDiscordId);
    }

}
