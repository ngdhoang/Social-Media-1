package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reaction_post", indexes = {
        @Index(name = "idx_post_id", columnList = "post_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_post_user", columnList = "post_id, user_id"),
        @Index(name = "idx_reaction_type", columnList = "reaction_type")
})
public class ReactionPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionPostId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity postEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    private EReactionTypeEntity reactionType;

    private Instant createAt;

    private Instant updateAt;

    @PreUpdate
    public void preUpdate() {
        updateAt = Instant.now();
    }

    @PrePersist
    public void prePersist() {
        createAt = Instant.now();
    }


    public ReactionPostEntity(PostEntity postEntity, UserEntity userEntity, EReactionTypeEntity reactionType) {
        this.postEntity = postEntity;
        this.userEntity = userEntity;
        this.reactionType = reactionType;
    }
}