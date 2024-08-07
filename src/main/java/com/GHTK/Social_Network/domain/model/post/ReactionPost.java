package com.GHTK.Social_Network.domain.model.post;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionPost {
  private Long reactionPostId;

  private Long postId;

  private EReactionType reactionType;

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

}