package com.GHTK.Social_Network.infrastructure.payload.dto;

import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShipUserDto {

    private UserBasicDto user;

    private Long mutualFriendsQuantity;

    private EFriendshipStatus status;

}
