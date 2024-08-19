package com.GHTK.Social_Network.domain.collection.chat;

import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    private Long userId;

    private String nickname;

    private String lastMsgSeen;

    private Instant lastTimeMsgSeen;

    private EStateUserGroup role;

    public Member(Long userId, String nickname, EStateUserGroup role, String lastMsgSeen) {
        this.userId = userId;
        this.nickname = nickname;
        this.role = role;
        this.lastMsgSeen = lastMsgSeen;
    }
}
