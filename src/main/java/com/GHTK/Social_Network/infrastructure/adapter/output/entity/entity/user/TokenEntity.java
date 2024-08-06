package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "token")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tokenId;

  private String fingerprinting;

  private String ;

  private boolean revoked;

  private boolean expired;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

}