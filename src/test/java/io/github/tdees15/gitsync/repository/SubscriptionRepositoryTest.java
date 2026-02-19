package io.github.tdees15.gitsync.repository;

import io.github.tdees15.gitsync.common.util.EncryptedStringConverter;
import io.github.tdees15.gitsync.common.util.EncryptionUtil;
import io.github.tdees15.gitsync.model.Subscription;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({EncryptionUtil.class, EncryptedStringConverter.class})
@TestPropertySource(properties = "encryption.secret.key=ZHVtbXlfMTYtYnl0ZV9rZXk=")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class SubscriptionRepositoryTest extends PostgresContainerTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void whenFindByChannelIdAndRepositoryOwnerAndRepositoryName_ThenReturnSubscription() {
        Subscription mockSubscription = createSubscription(
                "channel123",
                "tdees4",
                "gitsync",
                "guild123",
                "discordtdees4"
        );

        testEntityManager.persist(mockSubscription);
        testEntityManager.flush();

        Optional<Subscription> found = subscriptionRepository
                .findByChannelIdAndRepositoryOwnerAndRepositoryName(
                        mockSubscription.getChannelId(),
                        mockSubscription.getRepositoryOwner(),
                        mockSubscription.getRepositoryName()
                );

        assertTrue(found.isPresent());

        Subscription foundSubscription = found.get();

        assertEquals(mockSubscription.getRepositoryName(), foundSubscription.getRepositoryName());
        assertEquals(mockSubscription.getRepositoryOwner(), foundSubscription.getRepositoryOwner());
        assertEquals(mockSubscription.getChannelId(), foundSubscription.getChannelId());
    }

    @Test
    void whenDeleteByChannelIdAndRepositoryOwnerAndRepositoryName_ThenDeleteSubscription() {
        Subscription mockSubscription = createSubscription(
                "channel123",
                "tdees4",
                "gitsync",
                "guild123",
                "discordtdees4"
        );

        testEntityManager.persist(mockSubscription);
        testEntityManager.flush();

        subscriptionRepository
                .deleteByChannelIdAndRepositoryOwnerAndRepositoryName(
                        mockSubscription.getChannelId(),
                        mockSubscription.getRepositoryOwner(),
                        mockSubscription.getRepositoryName()
                );

        assertFalse(
                subscriptionRepository
                        .findByChannelIdAndRepositoryOwnerAndRepositoryName(
                                mockSubscription.getChannelId(),
                                mockSubscription.getRepositoryOwner(),
                                mockSubscription.getRepositoryName()
                        )
                        .isPresent()
        );
    }

    @Test
    void whenFindByChannelId_ThenReturnValidListOfSubscriptions() {
        Subscription mockSubscription = createSubscription(
                "channel123",
                "tdees4",
                "gitsync",
                "guild123",
                "discordtdees4"
        );

        Subscription otherMockSubscription = createSubscription(
                "channel456",
                "tdees4",
                "gitsync",
                "gitsync",
                "discordtdees4"
        );

        testEntityManager.persist(mockSubscription);
        testEntityManager.persist(otherMockSubscription);
        testEntityManager.flush();

        List<Subscription> subscriptions = subscriptionRepository
                .findByChannelId(mockSubscription.getChannelId());

        for (Subscription subscription : subscriptions) {
            assertEquals(mockSubscription.getChannelId(), subscription.getChannelId());
        }
    }

    @Test
    void whenDeleteByChannelId_ThenAllValidSubscriptionsAreDeleted() {
        Subscription mockSubscription = createSubscription(
                "channel123",
                "tdees4",
                "gitsync",
                "guild123",
                "discordtdees4"
        );

        Subscription otherMockSubscription = createSubscription(
                "channel456",
                "tdees4",
                "gitsync",
                "gitsync",
                "discordtdees4"
        );

        Subscription anotherMockSubscription = createSubscription(
                "channel123",
                "tdees4",
                "otherbot",
                "gitsync",
                "discordtdees4"
        );

        testEntityManager.persist(mockSubscription);
        testEntityManager.persist(otherMockSubscription);
        testEntityManager.persist(anotherMockSubscription);
        testEntityManager.flush();

        int numSubscriptionsWithOtherChannelId = 0;
        List<Subscription> allSubscriptions = subscriptionRepository
                .findAll();
        for (Subscription subscription : allSubscriptions) {
            if (!subscription.getChannelId().equals(mockSubscription.getChannelId()))
                numSubscriptionsWithOtherChannelId++;
        }

        subscriptionRepository.deleteByChannelId(mockSubscription.getChannelId());

        allSubscriptions = subscriptionRepository.findAll();

        assertEquals(numSubscriptionsWithOtherChannelId, allSubscriptions.size());

        for (Subscription subscription : allSubscriptions) {
            assertNotEquals(mockSubscription.getChannelId(), subscription.getChannelId());
        }
    }

    private Subscription createSubscription(String channelId, String repositoryOwner,
                                            String repositoryName, String guildId,
                                            String createdBy) {
        Subscription subscription = new Subscription();
        subscription.setRepositoryOwner(repositoryOwner);
        subscription.setChannelId(channelId);
        subscription.setRepositoryName(repositoryName);
        subscription.setGuildId(guildId);
        subscription.setCreatedBy(createdBy);
        subscription.setCreatedAt(LocalDateTime.now());
        return subscription;
    }

}
