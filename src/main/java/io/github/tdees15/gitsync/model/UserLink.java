package io.github.tdees15.gitsync.model;

import io.github.tdees15.gitsync.common.util.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name =  "user_links")
@Data
public class UserLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String discordId;

    @Column(nullable = false)
    private String githubId;

    private String githubUsername;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = EncryptedStringConverter.class)
    private String accessToken;

    @CreatedDate
    private LocalDateTime linkedAt;

}
