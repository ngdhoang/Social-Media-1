package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.MemberRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.SetMemBerNickNameRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.CreateGroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.GroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.MemberResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupPortInput {
  CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest);

  CreateGroupResponse changeGroupName(UpdateGroupRequest updateGroupRequest);

  CreateGroupResponse changeGroupBackground(MultipartFile backgroundImage, String groupId);

  CreateGroupResponse addMemberToGroup(MemberRequest memberRequest);

  CreateGroupResponse kickMemberToGroup(MemberRequest memberRequest);

  List<MemberResponse> getMemberToGroup(String groupId);

  MessageResponse outGroup(String groupId);

  MessageResponse changeNickname(SetMemBerNickNameRequest setMemBerNickNameRequest);

  MessageResponse changeStateGroup(String groupId, String state);

  List<GroupResponse> getMyGroups(PaginationRequest paginationRequest);

  CreateGroupResponse getMyGroup(String groupId);
}
