package io.github.tdees15.gitsync.repository;

import io.github.tdees15.gitsync.model.Subscription;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    @NotNull
    Optional<Subscription> findByChannelIdAndRepositoryName(String channelId, String repositoryName);
    void deleteByChannelIdAndRepositoryName(String channelId, String repositoryName);

    @NotNull
    List<Subscription> findByChannelId(String channelId);
    void deleteByChannelId(String channelId);

    @NotNull
    List<Subscription> findByRepositoryName(String repositoryName);
    void deleteByRepositoryName(String repositoryName);
}
