package com.GHTK.Social_Network.domain.model.friendShip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShip {
  private Long friendShipId;

  private EFriendshipStatus friendshipStatus;

  private Instant createAt;

  private Long userReceiveId;

  private Long userInitiatorId;
}
