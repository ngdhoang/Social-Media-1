package com.GHTK.Social_Network.infrastructure.payload.dto;

import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendShipUserDto {
  private UserDto user;

  private EFriendshipStatus status;

  private Long mutualFriendsQuantity;
}
