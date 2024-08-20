package com.GHTK.Social_Network.domain.event.friendship;

import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatFriendEvent {
    private FriendShip friendShip;
}
