package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long messageId;

  @Column(columnDefinition = "TEXT")
  private String message;

  private LocalDate sendDate;

  private int timeCallVideo;

  private Boolean isCallVideo;

  private Boolean isDelete;
}
