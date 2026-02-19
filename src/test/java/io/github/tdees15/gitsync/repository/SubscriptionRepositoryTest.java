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
        String targetChannelId = "channel123";
        String targetRepositoryOwner = "tdees4";
        String targetRepositoryName = "gitsync";

        Subscription mockSubscription = createSubscription(
                targetChannelId,
                targetRepositoryOwner,
                targetRepositoryName,
                "guild123",
                "discordtdees4"
        );

        testEntityManager.persist(mockSubscription);
        testEntityManager.flush();

        Optional<Subscription> target = subscriptionRepository
                .findByChannelIdAndRepositoryOwnerAndRepositoryName(
                        targetChannelId,
                        targetRepositoryOwner,
                        targetRepositoryName
                );

        assertTrue(target.isPresent());

        Subscription targetSubscription = target.get();

        assertEquals(targetChannelId, targetSubscription.getChannelId());
        assertEquals(targetRepositoryOwner, targetSubscription.getRepositoryOwner());
        assertEquals(targetRepositoryName, targetSubscription.getRepositoryName());
    }

    @Test
    void whenDeleteByChannelIdAndRepositoryOwnerAndRepositoryName_ThenDeleteSubscription() {
        String targetChannelId = "channel123";
        String targetRepositoryOwner = "tdees4";
        String targetRepositoryName = "gitsync";

        Subscription mockSubscription = createSubscription(
                targetChannelId,
                targetRepositoryOwner,
                targetRepositoryName,
                "guild123",
                "discordtdees4"
        );

        testEntityManager.persist(mockSubscription);
        testEntityManager.flush();

        subscriptionRepository
                .deleteByChannelIdAndRepositoryOwnerAndRepositoryName(
                        targetChannelId,
                        targetRepositoryOwner,
                        targetRepositoryName
                );

        assertFalse(
                subscriptionRepository
                        .findByChannelIdAndRepositoryOwnerAndRepositoryName(
                                targetChannelId,
                                targetRepositoryOwner,
                                targetRepositoryName
                        )
                        .isPresent()
        );
    }

    @Test
    void whenFindByChannelId_ThenReturnValidListOfSubscriptions() {
        String targetChannelId = "channel123";

        Subscription mockSubscription = createSubscription(
                targetChannelId,
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

        List<Subscription> targetSubscriptions = subscriptionRepository.findByChannelId(targetChannelId);

        targetSubscriptions.forEach((subscription ->
            assertEquals(targetChannelId, subscription.getChannelId())
        ));
    }

    @Test
    void whenDeleteByChannelId_ThenAllValidSubscriptionsAreDeleted() {
        String targetChannelId = "channel123";

        Subscription mockSubscription = createSubscription(
                targetChannelId,
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
                targetChannelId,
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
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        for (Subscription subscription : allSubscriptions) {
            if (!subscription.getChannelId().equals(targetChannelId))
                numSubscriptionsWithOtherChannelId++;
        }

        subscriptionRepository.deleteByChannelId(targetChannelId);

        allSubscriptions = subscriptionRepository.findAll();

        assertEquals(numSubscriptionsWithOtherChannelId, allSubscriptions.size());
        assertEquals(0, subscriptionRepository
                .findByChannelId(targetChannelId)
                .size());
    }

    @Test
    void whenFindByRepositoryOwnerAndRepositoryName_ThenReturnValidListOfSubscriptions() {
        String targetRepositoryOwner = "tdees4";
        String targetRepositoryName = "gitsync";

        Subscription mockSubscription = createSubscription(
                "channel123",
                targetRepositoryOwner,
                targetRepositoryName,
                "guild123",
                "discordtdees4"
        );

        Subscription otherMockSubscription = createSubscription(
                "channel456",
                targetRepositoryOwner,
                targetRepositoryName,
                "guild1234",
                "discordtdees4"
        );

        Subscription anotherMockSubscription = createSubscription(
                "channel123",
                "user123",
                targetRepositoryName,
                "guild123",
                "discordtdees4"
        );

        testEntityManager.persist(mockSubscription);
        testEntityManager.persist(otherMockSubscription);
        testEntityManager.persist(anotherMockSubscription);
        testEntityManager.flush();

        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        int numOfTargetedSubscriptions = 0;
        for (Subscription subscription : allSubscriptions) {
            if (subscription.getRepositoryName().equals(targetRepositoryName)
                    && subscription.getRepositoryOwner().equals(targetRepositoryOwner))
                numOfTargetedSubscriptions++;
        }

        List<Subscription> targetSubscriptions = subscriptionRepository.findByRepositoryOwnerAndRepositoryName(
                targetRepositoryOwner,
                targetRepositoryName
        );

        assertEquals(numOfTargetedSubscriptions, targetSubscriptions.size());
        targetSubscriptions.forEach((subscription) -> {
           assertEquals(targetRepositoryName, subscription.getRepositoryName());
           assertEquals(targetRepositoryOwner, subscription.getRepositoryOwner());
        });
    }

    @Test
    void whenDeleteByRepositoryOwnerAndRepositoryName_ThenAllValidSubscriptionsAreDeleted() {
        String targetRepositoryOwner = "tdees4";
        String targetRepositoryName = "gitsync";

        Subscription mockSubscription = createSubscription(
                "channel123",
                targetRepositoryOwner,
                targetRepositoryName,
                "guild123",
                "discordtdees4"
        );

        Subscription otherMockSubscription = createSubscription(
                "channel456",
                targetRepositoryOwner,
                targetRepositoryName,
                "guild1234",
                "discordtdees4"
        );

        Subscription anotherMockSubscription = createSubscription(
                "channel123",
                "user123",
                targetRepositoryName,
                "guild123",
                "discordtdees4"
        );

        testEntityManager.persist(mockSubscription);
        testEntityManager.persist(otherMockSubscription);
        testEntityManager.persist(anotherMockSubscription);
        testEntityManager.flush();

        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        int numOfTargetedSubscriptions = 0;
        for (Subscription subscription : allSubscriptions) {
            if (subscription.getRepositoryName().equals(targetRepositoryName)
                    && subscription.getRepositoryOwner().equals(targetRepositoryOwner))
                numOfTargetedSubscriptions++;
        }
        int remainingSubscriptions = allSubscriptions.size() - numOfTargetedSubscriptions;

        subscriptionRepository.deleteByRepositoryOwnerAndRepositoryName(targetRepositoryOwner, targetRepositoryName);

        allSubscriptions = subscriptionRepository.findAll();

        assertEquals(remainingSubscriptions, allSubscriptions.size());
        assertEquals(0, subscriptionRepository
                .findByRepositoryOwnerAndRepositoryName(targetRepositoryOwner, targetRepositoryName)
                .size());
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
