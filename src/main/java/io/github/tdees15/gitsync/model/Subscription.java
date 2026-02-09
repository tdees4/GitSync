package io.github.tdees15.gitsync.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Subscription {
    @Id
    @GeneratedValue
    private long id;

    private String guildId;
    private String channelId;
    private String repositoryOwner;
    private String repositoryName;

    @Column(columnDefinition = "jsonb")
    private String filterJson;

    @ElementCollection
    @CollectionTable(name = "subscription_filters")
    private List<Integer> filters; // TODO: Create FilterConfig class & replace Integer with FilterConfig
}
