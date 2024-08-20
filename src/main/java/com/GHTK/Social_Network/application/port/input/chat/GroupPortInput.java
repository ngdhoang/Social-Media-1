package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.CreateGroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupPortInput {
  CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest);

  CreateGroupResponse changeGroupName(UpdateGroupRequest updateGroupRequest);

  CreateGroupResponse changeGroupBackground(MultipartFile backgroundImage, String groupId);

  CreateGroupResponse addMemberToGroup(MemberRequest memberRequest);

  CreateGroupResponse kickMemberToGroup(MemberRequest memberRequest);

  MessageResponse outGroup(String groupId);

  MessageResponse changeNickname(SetMemBerNickNameRequest setMemBerNickNameRequest);

  MessageResponse changeStateGroup(String groupId, String state);

  List<GroupResponse> getMyGroups(PaginationRequest paginationRequest);
}
