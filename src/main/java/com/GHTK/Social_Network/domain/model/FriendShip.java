package com.GHTK.Social_Network.domain.model;

import com.GHTK.Social_Network.domain.model.user.User;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShip {
  private Long friendShipId;

  private EFriendshipStatus friendshipStatus;

  private LocalDate createAt;

  private Long userReceiveId;

  private Long userInitiatorId;

  private User user;

  @PrePersist
  public void prePersist() {
    createAt = LocalDate.now();
  }
}
