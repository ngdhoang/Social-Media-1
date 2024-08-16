package com.GHTK.Social_Network.infrastructure.adapter.output.entity.node;

import lombok.Data;

@Data
public class FriendSuggestion {
    private UserNode potentialFriend;
    private int totalScore;

    public FriendSuggestion(UserNode potentialFriend, int totalScore) {
        this.potentialFriend = potentialFriend;
        this.totalScore = totalScore;
    }
}