package com.GHTK.Social_Network.domain.model.post;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

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

  private LocalDate createAt;

  private LocalDate updateAt;

  @PrePersist
  public void prePersist() {
    this.createAt = LocalDate.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updateAt = LocalDate.now();
  }

}