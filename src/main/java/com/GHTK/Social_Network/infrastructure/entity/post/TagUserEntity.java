package com.GHTK.Social_Network.infrastructure.entity.post;

import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagUserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tagUserId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private PostEntity postEntity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;
}
