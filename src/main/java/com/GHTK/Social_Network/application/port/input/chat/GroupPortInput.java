package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetMemBerNickNameRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.CreateGroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

import java.util.List;

public interface GroupPortInput {
  CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest);

  CreateGroupResponse updateGroup(UpdateGroupRequest updateGroupRequest);

  MessageResponse deleteGroup(String groupId);

  CreateGroupResponse addNewMemberToGroup(String groupId, List<Member> members);

  MessageResponse outGroup(String groupId);

  MessageResponse setMemberNickName(SetMemBerNickNameRequest setMemBerNickNameRequest);

  List<GroupResponse> getAllMyGroups( PaginationRequest paginationRequest);

//  GroupResponse setMemberRole(ChangeMemberRoleRequest changeMemberRoleRequest);
}
