package com.GHTK.Social_Network.domain.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

public class ImagePost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imagePostId;

  private String pictureUrl;

  private LocalDate createAt;

  private Boolean isDelete;
}
