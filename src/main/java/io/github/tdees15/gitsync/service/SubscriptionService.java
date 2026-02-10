package io.github.tdees15.gitsync.service;

import io.github.tdees15.gitsync.model.FilterConfig;
import io.github.tdees15.gitsync.model.Subscription;
import io.github.tdees15.gitsync.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public Subscription createSubscription(String guildId, String channelId,
                                           String repository, String userId) {
        String[] parts = repository.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Repository must be structured like \"tdees4/repo\"!"
            );
        }

        String owner = parts[0].trim();
        String name = parts[1].trim();

        if (owner.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Repository owner and name cannot be empty!"
            );
        }

        Optional<Subscription> existing = subscriptionRepository
                .findByChannelIdAndRepositoryOwnerAndRepositoryName(channelId, owner, name);

        if (existing.isPresent()) {
            throw new IllegalStateException(
                    "This channel is already subscribed to " + repository + "!"
            );
        }

        Subscription subscription = new Subscription();
        subscription.setGuildId(guildId);
        subscription.setChannelId(channelId);
        subscription.setRepositoryOwner(owner);
        subscription.setRepositoryName(name);
        subscription.setCreatedBy(userId);
        subscription.setCreatedAt(LocalDateTime.now());

        FilterConfig defaultFilter = new FilterConfig(
                "*",
                List.of("*"),
                List.of("*")
        );

        subscription.setFilters(List.of(defaultFilter));

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void deleteSubscription(String channelId, String repository) {
        String[] parts = repository.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Repository must be structured like \"tdees4/repo\"!"
            );
        }

        String owner = parts[0].trim();
        String name = parts[1].trim();

        Optional<Subscription> subscription = subscriptionRepository
                .findByChannelIdAndRepositoryOwnerAndRepositoryName(channelId, owner, name);

        if (subscription.isEmpty()) {
            throw new IllegalStateException(
                    "No subscription exists for " + repository + " in channel " + channelId
            );
        }

        subscriptionRepository.delete(subscription.get());
    }

    public List<Subscription> getSubscriptionsByChannelId(String channelId) {
        return subscriptionRepository.findByChannelId(channelId);
    }

}
