package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

public class Group {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long groupId;

  private String groupName;

  private LocalDate foundedDate;

  private Long leaderId;

  private Boolean isDelete;
}
