package com.GHTK.Social_Network.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity

public class ImageComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imageCommentId;

  private String imageUrl;

  private LocalDate createAt;

  private Boolean isDelete;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "commentId", nullable = false)
  private Comment comment;
}
