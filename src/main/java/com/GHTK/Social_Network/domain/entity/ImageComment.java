package com.GHTK.Social_Network.domain.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

public class ImageComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imageCommentId;

  private String imageUrl;

  private LocalDate createAt;

  private Boolean isDelete;
}
