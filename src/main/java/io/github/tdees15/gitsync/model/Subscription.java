package io.github.tdees15.gitsync.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscriptions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"channelId", "repositoryOwner", "repositoryName"}
        ))
@Data
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String guildId;

    @Column(nullable = false)
    private String channelId;

    @Column(nullable = false)
    private String repositoryOwner;

    @Column(nullable = false)
    private String repositoryName;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "subscription_filters",
                        joinColumns = @JoinColumn(name = "subscription_id", referencedColumnName = "id"))
    private List<FilterConfig> filters = new ArrayList<>();

    private String webhookSecret; // Optional
}
