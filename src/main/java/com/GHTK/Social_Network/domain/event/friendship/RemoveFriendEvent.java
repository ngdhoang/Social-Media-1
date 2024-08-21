package com.GHTK.Social_Network.domain.event.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveFriendEvent {
    private Long FriendshipId;
}
