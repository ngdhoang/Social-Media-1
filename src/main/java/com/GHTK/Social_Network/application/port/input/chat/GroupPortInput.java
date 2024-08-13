package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.requests.ChangeMemberRoleRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

public interface GroupPortInput {
  GroupResponse createGroup(CreateGroupRequest createGroupRequest);

  GroupResponse updateGroup(UpdateGroupRequest updateGroupRequest);

  MessageResponse deleteGroup(String groupId);

  GroupResponse setMemberRole(ChangeMemberRoleRequest changeMemberRoleRequest);

}
