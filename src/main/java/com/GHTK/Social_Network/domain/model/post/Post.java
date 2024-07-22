package com.GHTK.Social_Network.domain.model.post;

import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
  private Long postId;

  private String content;

  private Date createAt;

  private Date updateAt;

  private EPostStatus postStatus;

  private Long userId;

  private List<Long> imagePostIds;

  private List<Long> tagUserIds;

  private List<Long> reactionPostIds;

  private List<Long> commentIds;

  @PrePersist
  public void prePersist() {
    createAt = new Date();
  }
}
