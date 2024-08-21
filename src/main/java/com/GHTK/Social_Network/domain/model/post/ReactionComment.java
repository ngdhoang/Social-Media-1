package com.GHTK.Social_Network.domain.model.post;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReactionComment {
    private Long reactionCommentId;

    private EReactionType reactionType;

    private Long commentId;

    private Long userId;

    private Instant createAt;

    private Instant updateAt;

    @PrePersist
    public void prePersist() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = Instant.now();
    }

    public ReactionComment(EReactionType reactionType, Long commentId, Long userId) {
        this.reactionType = reactionType;
        this.commentId = commentId;
        this.userId = userId;
    }
}