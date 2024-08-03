package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "reaction_comment")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReactionCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionCommentId;

    @Enumerated(EnumType.STRING)
    private EReactionTypeEntity reactionType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity commentEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

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

    public ReactionCommentEntity(EReactionTypeEntity reactionType, CommentEntity commentEntity, UserEntity userEntity) {
        this.reactionType = reactionType;
        this.commentEntity = commentEntity;
        this.userEntity = userEntity;
    }
}