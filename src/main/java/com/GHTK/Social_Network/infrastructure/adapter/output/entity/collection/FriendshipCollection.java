package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendshipCollection {
    @Id
    private String id;

    private Long userId;

    private LinkedList<Long> listFriendId;

    private LinkedList<Long> listBlockId;

    private LinkedList<Long> listBlockedId;

    public FriendshipCollection(Long userId) {
        this.userId = userId;
        this.listFriendId = new LinkedList<>();
        this.listBlockId = new LinkedList<>();
        this.listBlockedId = new LinkedList<>();
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
