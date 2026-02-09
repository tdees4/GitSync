package io.github.tdees15.gitsync.model;

import io.github.tdees15.gitsync.common.util.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name =  "user_links")
public class UserLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String discordId;

    @Column(nullable = false)
    @Getter
    @Setter
    private String githubId;

    @Getter
    @Setter
    private String githubUsername;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = EncryptedStringConverter.class)
    @Getter
    @Setter
    private String accessToken;

    @CreatedDate
    @Getter
    @Setter
    private LocalDateTime linkedAt;

}
