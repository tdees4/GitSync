package io.github.tdees15.gitsync.service;

import io.github.tdees15.gitsync.model.Subscription;
import io.github.tdees15.gitsync.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createSubscription_ValidRepo_ReturnsSubscription() {
        when(subscriptionRepository.findByChannelIdAndRepositoryOwnerAndRepositoryName(
                any(), any(), any()
        )).thenReturn(Optional.empty());

        when(subscriptionRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(subscriptionRepository.saveAndFlush(any()))
                .thenAnswer(i -> i.getArgument(0));

        Subscription result = subscriptionService.createSubscription(
                "guild123",
                "channel123",
                "tdees4/gitsync",
                "user123"
        );

        assertNotNull(result);
        assertEquals("tdees4", result.getRepositoryOwner());
        assertEquals("gitsync", result.getRepositoryName());
        assertEquals("channel123", result.getChannelId());
        verify(subscriptionRepository, times(1)).save(any());
    }

    @Test
    void createSubscription_InvalidRepoFormat_ThrowsException() {
        String invalidRepo = "invalid-repo";

        assertThrows(IllegalArgumentException.class, () ->
            subscriptionService.createSubscription(
                    "guild123",
                    "channel123",
                    invalidRepo,
                    "user123"
            )
        );

        // Verify save was never ran
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void createSubscription_AlreadySubscribed_ThrowsException() {
        when(subscriptionRepository.findByChannelIdAndRepositoryOwnerAndRepositoryName(
                any(), any(), any()
        )).thenReturn(Optional.of(new Subscription()));

        assertThrows(IllegalStateException.class, () ->
                subscriptionService.createSubscription(
                        "guild123",
                        "channel123",
                        "tdees4/gitsync",
                        "user123"
                )
        );
    }

    @Test
    void createSubscription_DefaultFilter_IsAllEvents() {
        when(subscriptionRepository.findByChannelIdAndRepositoryOwnerAndRepositoryName(
                any(), any(), any()
        )).thenReturn(Optional.empty());

        when(subscriptionRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(subscriptionRepository.saveAndFlush(any()))
                .thenAnswer(i -> i.getArgument(0));

        Subscription result = subscriptionService.createSubscription(
                "guild123",
                "channel123",
                "tdees4/gitsync",
                "user123"
        );

        assertFalse(result.getFilters().isEmpty());
        assertEquals("*", result.getFilters().getFirst().getBranchPattern());
        assertTrue(result.getFilters().getFirst().getEventTypes().contains("*"));
        assertTrue(result.getFilters().getFirst().getActions().contains("*"));
    }

    @Test
    void deleteSubscription_ValidExistingSubscription_ThrowsNoExceptions() {
        when(subscriptionRepository.findByChannelIdAndRepositoryOwnerAndRepositoryName(
                any(), any(), any()
        )).thenReturn(Optional.of(new Subscription()));

        subscriptionService.deleteSubscription("channel123", "tdees4/gitsync");
    }

    @Test
    void deleteSubscription_InvalidRepoName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.deleteSubscription(
                        "channel123",
                        "tdees4-gitsync"
                )
        );
    }

    @Test
    void deleteSubscription_SubscriptionDoesNotExist_ThrowsException() {
        when(subscriptionRepository.findByChannelIdAndRepositoryOwnerAndRepositoryName(
                any(), any(), any()
        )).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                subscriptionService.deleteSubscription(
                        "channel123",
                        "tdees4/gitsync"
                )
        );
    }
}
