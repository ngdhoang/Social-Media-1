package com.GHTK.Social_Network.infrastructure.payload.dto;

import lombok.Data;

@Data
public class SearchDto {
  private Long userId;

  private String firstName;

  private String lastName;

  private String email;

  private String avatar;
}
