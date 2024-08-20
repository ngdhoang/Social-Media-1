package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.application.port.input.chat.OfflineOnlineInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.domain.collection.chat.*;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.GroupMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.CreateGroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService implements GroupPortInput {
  private final FriendShipPort friendShipPort;
  private final GroupPort groupPort;
  private final AuthPort authPort;
  private final WebsocketClientPort websocketClientPort;
  private final OfflineOnlineInput offlineOnlineInput;
  private final CloudPort cloudPort;

  private final GroupMapper groupMapper;

  @Override
  public CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest) {
    User currentUser = authPort.getUserAuth();

    validateFriendship(currentUser.getUserId(), createGroupRequest.getMembers());
    List<Member> members = getMembersWithAdminByListId(createGroupRequest.getMembers(), currentUser);
    Group newGroup = groupMapper.createGroupToDomain(
            createGroupRequest
    );
    newGroup.setMembers(members);
    CreateGroupResponse createGroupResponse = groupMapper.groupToCreateResponse(groupPort.saveGroup(newGroup));
    setMemberInGroup(members, createGroupResponse.getGroupId());

    Message message = Message.builder()
            .userAuthId(currentUser.getUserId())
            .content(currentUser.getUserEmail() + " created group")
            .msgType(EMessageType.MESSAGE)
            .build();
    websocketClientPort.sendListUserAndSave(message, members.stream().map(
            Member::getUserId
    ).toList());

    return createGroupResponse;
  }

  @Override
  public CreateGroupResponse changeGroupName(UpdateGroupRequest updateGroupRequest) {
    User currentUser = authPort.getUserAuth();
    Group group = getGroup(updateGroupRequest.getGroupId());

    validateGroupForMe(group, currentUser.getUserId());
    validatePermissionGroup(group, currentUser.getUserId());

    group.setGroupName(updateGroupRequest.getGroupName());
    return groupMapper.groupToCreateResponse(groupPort.saveGroup(group));
  }

  @Override
  public CreateGroupResponse changeGroupBackground(MultipartFile backgroundImage, String groupId) {
    User currentUser = authPort.getUserAuth();
    Group group = getGroup(groupId);

    validateGroupForMe(group, currentUser.getUserId());
    validatePermissionGroup(group, currentUser.getUserId());

    String url = (String) cloudPort.uploadPictureByFile(backgroundImage, ImageHandlerPortInput.MAX_SIZE_AVATAR).get("url");
    group.setGroupBackground(url);

    return groupMapper.groupToCreateResponse(groupPort.saveGroup(group));
  }

  @Override
  public CreateGroupResponse addMemberToGroup(MemberRequest memberRequest) {
    User currentUser = authPort.getUserAuth();
    Group group = getGroup(memberRequest.getGroupId());

    validateGroupForMe(group, currentUser.getUserId());
    validatePermissionGroup(group, currentUser.getUserId());
    validateFriendship(currentUser.getUserId(), memberRequest.getMemberId());

    List<Long> existingMemberIds = group.getMembers().stream()
            .map(Member::getUserId)
            .toList();
    List<Long> newMemberIds = memberRequest.getMemberId().stream()
            .filter(memberId -> !existingMemberIds.contains(memberId))
            .toList();

    addNewMembersToGroup(group, newMemberIds, currentUser, existingMemberIds);

    return groupMapper.groupToCreateResponse(group);
  }


  @Override
  public CreateGroupResponse kickMemberToGroup(MemberRequest memberRequest) {
    User currentUser = authPort.getUserAuth();
    Group group = getGroup(memberRequest.getGroupId());

    validateGroupForMe(group, currentUser.getUserId());
    validatePermissionGroup(group, currentUser.getUserId());
    validateFriendship(currentUser.getUserId(), memberRequest.getMemberId());

    List<Long> existingMemberIds = group.getMembers().stream()
            .map(Member::getUserId)
            .toList();
    List<Long> newMemberIds = existingMemberIds.stream()
            .filter(memberId -> !memberRequest.getMemberId().contains(memberId))
            .toList();

    kickMembersToGroup(group, newMemberIds, currentUser, existingMemberIds);

    return groupMapper.groupToCreateResponse(group);
  }

  @Override
  public MessageResponse outGroup(String groupId) {
    User currentUser = authPort.getUserAuth();
    Group group = getGroup(groupId);

    validateGroupForMe(group, currentUser.getUserId());

    // Check if the current user is a member of the group
    boolean isMember = group.getMembers().stream()
            .anyMatch(member -> member.getUserId().equals(currentUser.getUserId()));
    if (!isMember) {
      throw new CustomException("You are not a member of this group and cannot out group", HttpStatus.FORBIDDEN);
    }

    UserCollectionDomain userCollection = authPort.getUserCollectionById(currentUser.getUserId());
    UserGroup userGroup = userCollection.getUserGroupInfoList().stream()
            .filter(ug -> ug.getGroupId().equals(groupId))
            .findFirst()
            .orElse(null);

    userGroup.setLastGroupName(group.getGroupName());

    return new MessageResponse("Out group successful!");
  }

  @Override
  public MessageResponse changeNickname(SetMemBerNickNameRequest setMemBerNickNameRequest) {
    User currentUser = authPort.getUserAuth();
    Group group = getGroup(setMemBerNickNameRequest.getGroupId());

    validateGroupForMe(group, currentUser.getUserId());
    validatePermissionGroup(group, currentUser.getUserId());

    return null;
  }

  @Override
  public MessageResponse changeStateGroup(String groupId, String state) {
    return null;
  }

  @Override
  public List<GroupResponse> getMyGroups(PaginationRequest paginationRequest) {
    User currentUser = authPort.getUserAuth();

    UserCollectionDomain userGroupInfoList = groupPort.getUserGroups(
            currentUser.getUserId(),
            paginationRequest.getPage(),
            paginationRequest.getSize()
    );
//        List<GroupResponse> groupResponses = userGroupInfoList.getUserGroupInfoList().stream()
//                .map(userGroup -> groupInfoToResponse(userGroup, currentUser.getUserId()))
//                .collect(Collectors.toList());
    return null;
  }

  private GroupResponse groupInfoToResponse(UserGroup userGroup, Long currentId) {
    Group group = getGroup(userGroup.getGroupId());
    GroupResponse groupResponse = new GroupResponse();
    groupResponse.setGroupId(userGroup.getGroupId());
    groupResponse.setGroupName(
            userGroup.getLastGroupName() != null ? group.getGroupName() : userGroup.getLastGroupName()
    );
    groupResponse.setGroupBackground(group.getGroupBackground());
    if (group.getGroupType().equals(EGroupType.PERSONAL)) {
      Long userId = getUserInGroupPersonal(group.getMembers(), currentId);
      groupResponse.setOnline(offlineOnlineInput.isUserOnline(userId));
      UserCollectionDomain userCollectionDomain = authPort.getUserCollectionById(userId);
      groupResponse.setLastActive(userCollectionDomain.getLastActive());
    }
    return groupResponse;
  }

  private Long getUserInGroupPersonal(List<Member> members, Long currentId) {
    return members.get(0).getUserId().equals(currentId) ? members.get(1).getUserId() : members.get(0).getUserId();
  }

  private void validateFriendship(Long currentId, List<Long> userIds) {
    userIds.forEach(userId -> {
      if (friendShipPort.isBlock(currentId, userId)
              || friendShipPort.isDeleteUser(userId)
              || !friendShipPort.isFriend(userId, currentId)
      ) {
        throw new CustomException("UserId " + userId + "don't exist", HttpStatus.BAD_REQUEST);
      }
    });
  }


  private void addNewMembersToGroup(Group group, List<Long> newMemberIds, User currentUser, List<Long> existingMemberIds) {
    newMemberIds.forEach(id -> {
      User newMember = authPort.getUserById(id);
      sendNotificationsAddMember(currentUser, newMember, existingMemberIds);

      groupPort.addMember(group.getId(),
              Member.builder()
                      .userId(id)
                      .role(EStateUserGroup.USER)
                      .build()
      );
    });
  }

  private void kickMembersToGroup(Group group, List<Long> newMemberIds, User currentUser, List<Long> existingMemberIds) {
    newMemberIds.forEach(id -> {
      User newMember = authPort.getUserById(id);
      sendNotificationsAddMember(currentUser, newMember, existingMemberIds);
      kickMemberToGroup(group, id);
    });
  }

  private void sendNotificationsAddMember(User currentUser, User newMember, List<Long> existingMemberIds) {
    Message message = websocketClientPort.createNotificationMessage(
            currentUser.getUserId(),
            currentUser.getUserEmail() + " added " + newMember.getUserEmail() + " to group"
    );
    websocketClientPort.sendListUserAndSave(message, existingMemberIds);
    websocketClientPort.sendUserAndNotSave(message, "/app/channel/" + newMember.getUserId());
  }

  private void addMemberToGroup(Group group, Long userId) {
    group.getMembers().add(
            Member.builder()
                    .userId(userId)
                    .role(EStateUserGroup.USER)
                    .build()
    );
  }

  private void kickMemberToGroup(Group group, Long userId) {
    group.getMembers().remove(
            Member.builder()
                    .userId(userId)
                    .role(EStateUserGroup.USER)
                    .build()
    );
  }

  private Member userToMember(User member, String role) {
    EStateUserGroup newRole;
    if (role.equals("MANAGER")) {
      newRole = EStateUserGroup.MANAGER;
    } else if (role.equals("ADMIN")) {
      newRole = EStateUserGroup.ADMIN;
    } else {
      newRole = EStateUserGroup.USER;
    }
    return new Member(
            member.getUserId(),
            null, // default nickname
            newRole,
            null
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
              List<UserGroup> userGroupInfoList = userCollectionDomain.getUserGroupInfoList();
              if (userGroupInfoList == null) {
                userCollectionDomain.setUserGroupInfoList(new ArrayList<>());
              }
              userCollectionDomain.getUserGroupInfoList().add(new UserGroup(
                      groupId,
                      null,
                      null
              ));
              groupPort.saveUser(userCollectionDomain);
            }
    );
  }

  private void validateGroupForMe(Group group, Long userId) {
    if (group == null) {
      throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
    }
    if (!groupPort.isUserInGroup(userId, group.getId())) {
      throw new CustomException("You are not a member of this group", HttpStatus.FORBIDDEN);
    }
  }

  private void validatePermissionGroup(Group group, Long currentUserId) {
    if (isPublicGroup(group)) {
      return;
    }

    if (!isUserAuthorized(group, currentUserId)) {
      throw new CustomException("You do not have permission to change this group", HttpStatus.FORBIDDEN);
    }
  }

  private boolean isPublicGroup(Group group) {
    return group.getGroupType().equals(EGroupType.PERSONAL) ||
            EGroupType.GROUP.isRolePublic(group.getGroupType().getAllowedRoles().get(0));
  }

  private boolean isUserAuthorized(Group group, Long userId) {
    return group.getMembers().stream()
            .anyMatch(member -> member.getUserId().equals(userId) &&
                    !member.getRole().equals(EStateUserGroup.USER));
  }

  private List<Long> getGroupMemberIds(List<Member> members) {
    return members.stream().map(Member::getUserId).toList();
  }

  private Group deleteMemberInGroup(String groupId, Long userId) {
    Group group = getGroup(groupId);
    List<Member> members = group.getMembers().stream()
            .filter(m -> !m.getUserId().equals(userId))
            .toList();
    group.setMembers(members);
    return group;
  }

  private Group getGroup(String groupId) {
    boolean isGroupPersonal = groupId.contains("_");
    return isGroupPersonal ? groupPort.getGroupForPersonal(groupId) : groupPort.getGroupForGroup(groupId);
  }

}
