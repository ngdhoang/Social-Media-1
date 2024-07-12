package com.GHTK.Social_Network.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  @Column(columnDefinition = "TEXT")
  private String content;

  private LocalDate createdAt;

  @Enumerated(EnumType.STRING)
  private EPostStatus postStatus;
}
