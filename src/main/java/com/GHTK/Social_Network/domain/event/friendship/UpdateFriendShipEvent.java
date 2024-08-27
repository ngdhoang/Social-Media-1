package com.GHTK.Social_Network.domain.event.friendship;

import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateFriendShipEvent {
    private FriendShip friendship;

    private EFriendshipStatus status;

    private EFriendshipStatus previousStatus;
}
