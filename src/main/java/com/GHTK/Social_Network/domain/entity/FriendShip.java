package com.GHTK.Social_Network.domain.entity;

import com.GHTK.Social_Network.domain.entity.user.EStatusUser;
import com.GHTK.Social_Network.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class FriendShip {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long friendShipId;

  @Enumerated(EnumType.STRING)
  private EFriendshipStatus friendshipStatus;

  private LocalDate createAt;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "userReceiverId", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "userInitiatorId", nullable = false)
  private User user1;

}
