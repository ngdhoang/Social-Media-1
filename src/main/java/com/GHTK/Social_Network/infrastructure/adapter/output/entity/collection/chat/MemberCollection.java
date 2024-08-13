package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberCollection {
    private String userId;

    private String nickname;

    private String lastMsgSeen;

    private EMemberRole role;
}
