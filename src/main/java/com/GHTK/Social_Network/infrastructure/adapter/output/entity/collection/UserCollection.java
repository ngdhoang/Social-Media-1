package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.LinkedList;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCollection {
    @Id
    private String id;

    private Long userId;

    private boolean isDelete;

//    private Instant lastActive;

    private LinkedList<Long> listFriendId;

    private LinkedList<Long> listBlockId;

    private LinkedList<Long> listBlockedId;

    public UserCollection(Long userId) {
        this.userId = userId;
        this.listFriendId = new LinkedList<>();
        this.listBlockId = new LinkedList<>();
        this.listBlockedId = new LinkedList<>();
        this.isDelete = false;
    }

    public void addFriend(Long friendId) {
        listFriendId.add(friendId);
    }

    public void addBlock(Long blockId) {
        listBlockId.add(blockId);
    }

    public void addBlocked(Long blockedId) {
        listBlockedId.add(blockedId);
    }

}
