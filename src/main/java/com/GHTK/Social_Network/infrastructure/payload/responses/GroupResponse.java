package com.GHTK.Social_Network.infrastructure.payload.responses;

import lombok.Data;

import java.time.Instant;

@Data
public class GroupResponse {
    private String groupId;

    private String groupName;

    private String lastMessage;

    private String groupBackground;

    private boolean isOnline;

    private Instant lastActive;

    private Long messageUnreadCount;
}
