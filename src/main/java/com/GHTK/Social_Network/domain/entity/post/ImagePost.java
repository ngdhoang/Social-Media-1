package com.GHTK.Social_Network.domain.entity.post;

import com.GHTK.Social_Network.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagePost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imagePostId;

  private String imageUrl;

  private LocalDate createAt;

  private Boolean isDelete;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
