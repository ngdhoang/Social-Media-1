package com.GHTK.Social_Network.domain.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCollectionDomain {
    private String id;

    private Long userId;

    private boolean isDelete;

    private Instant lastActive;

    private List<UserGroup> userGroupInfoList;

    private LinkedList<Long> listFriendId;

    private LinkedList<Long> listBlockId;

    private LinkedList<Long> listBlockedId;
}
