package com.GHTK.Social_Network.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  private LocalDate createUp;

  private Boolean isDelete;

  @Column(columnDefinition = "TEXT")
  private String content;
}
