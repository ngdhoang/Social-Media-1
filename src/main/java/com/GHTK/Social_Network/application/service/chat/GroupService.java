package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.GroupMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService implements GroupPortInput {
  private final FriendShipPort friendShipPort;
  private final GroupPort groupPort;
  private final AuthPort authPort;
  private final WebsocketClientPort websocketClientPort;

  private final GroupMapper groupMapper;

  @Override
  public GroupResponse createGroup(CreateGroupRequest createGroupRequest) {
    User currentUser = authPort.getUserAuth();

    validateFriendship(currentUser.getUserId(), createGroupRequest.getMembers());
    List<Member> members = getMembersWithAdminByListId(createGroupRequest.getMembers(), currentUser);
    Group newGroup = groupMapper.createGroupToDomain(
            createGroupRequest
    );
    GroupResponse groupResponse = groupMapper.groupToResponse(groupPort.saveGroup(newGroup));

    setMemberInGroup(members, groupResponse.getGroupId());
    return groupResponse;
  }

  private void validateFriendship(Long currentId, List<Long> userIds) {
    userIds.forEach(userId -> {
      if (friendShipPort.isBlock(currentId, userId)
              || friendShipPort.isDeleteUser(userId)
              || friendShipPort.isFriend(userId, currentId)
      ) {
        throw new CustomException("UserId " + userId + "don't exist", HttpStatus.BAD_REQUEST);
      }
    });
  }

  private Member userToMember(User member, String role) {
    return new Member(
            member.getUserId(),
            null, // default nickname
            null,
            role
    );
  }

  private List<Member> getMembersByListId(List<Long> memberIds) {
    return memberIds.stream().map(
            id -> {
              User member = authPort.getUserById(id);
              return userToMember(member, "MEMBER");
            }
    ).toList();
  }

  private List<Member> getMembersWithAdminByListId(List<Long> memberIds, User admin) {
    Member adminMember = userToMember(admin, "ADMIN");
    List<Member> members = new ArrayList<>(getMembersByListId(memberIds));
    members.add(adminMember);
    return members;
  }

  private void setMemberInGroup(List<Member> members, String groupId) {
    members.stream().forEach(
            member -> {
              UserCollectionDomain userCollectionDomain = authPort.getUserCollectionById(member.getUserId());
              userCollectionDomain.getUserGroupInfoList().add(new UserGroup(
                      groupId,
                      member.getRole()
              ));
              groupPort.saveUser(userCollectionDomain);
            }
    );
  }
}
