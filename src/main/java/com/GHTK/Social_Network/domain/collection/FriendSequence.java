package com.GHTK.Social_Network.domain.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendSequence {
    private Long userId;
    private LinkedList<Long> listFriendId;
}
