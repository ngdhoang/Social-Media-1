package com.ghtk.social_network.domain.model;

import com.ghtk.social_network.domain.model.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tokens {

  private Long tokenId;

  private String token;

  private ETokenType tokenType = ETokenType.BEARER;

  private boolean revoked;

  private boolean expired;



}