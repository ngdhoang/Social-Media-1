package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "reaction_post")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private LocalDate createAt;

    private LocalDate updateAt;

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDate.now();
    }

    @PrePersist
    public void prePersist() {
        createAt = LocalDate.now();
    }


    public ReactionPostEntity(PostEntity postEntity, UserEntity userEntity, EReactionTypeEntity reactionType) {
        this.postEntity = postEntity;
        this.userEntity = userEntity;
        this.reactionType = reactionType;
    }
}