package com.GHTK.Social_Network.domain.entity;

import com.GHTK.Social_Network.domain.entity.user.EStatusUser;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class FriendShip {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long friendShipId;

  @Enumerated(EnumType.STRING)
  private EFriendshipStatus friendshipStatus;

  private LocalDate createAt;

}
