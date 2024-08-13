package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.EMemberRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeMemberRoleRequest {
    private  Long UserId;

    private  String groupId;

    private EMemberRole newRole;
}
