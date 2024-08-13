package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.chat.EMemberRole;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.GroupMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.MemberDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.ChangeMemberRoleRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService implements GroupPortInput {
  private final FriendShipPort friendShipPort;
  private final GroupPort groupPort;
  private final AuthPort authPort;
  private final GroupMapper groupMapper;
  private final UserMapper userMapper;

  @Override
  public GroupResponse createGroup(CreateGroupRequest createGroupRequest) {
    //Check di con lon
    User currentUser = authPort.getUserAuth();
    validateFriendship(currentUser.getUserId(), createGroupRequest.getMembers());
    Group newGroup = groupMapper.createGroupToDomain(
            createGroupRequest,
            getMembersWithAdminByListId(createGroupRequest.getMembers(), currentUser)
    );
    Group g = groupPort.createGroup(newGroup);
    System.out.println(g);
    System.out.println(g.getGroupType().getClass());
    return GroupResponse.builder().groupId(g.getGroupId())
            .groupBackground(g.getGroupBackground())
            .groupName(g.getGroupName())
            .groupType(g.getGroupType())
            .members(g.getMembers().stream().map(member ->
                            MemberDto.builder()
                                    .userId(member.getUserId())
                                    .nickname(member.getNickname())
                                    .role(member.getRole().toString())
                                    .build())
                    .collect(Collectors.toList()))
                    .build();
  }

  @Override
  public GroupResponse updateGroup(UpdateGroupRequest updateGroupRequest) {
    User currentUser = authPort.getUserAuth();

    // Validate the group ID
    Group existingGroup = groupPort.getGroup(updateGroupRequest.getId());
    if (existingGroup == null) {
      throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
    }

    // Check if the current user is a member of the group
    boolean isMember = existingGroup.getMembers().stream()
            .anyMatch(member -> member.getUserId().equals(currentUser.getUserId()));

    if (!isMember) {
      throw new CustomException("You are not a member of this group and cannot update it", HttpStatus.FORBIDDEN);
    }

    // Update group details
    Group updatedGroup = groupMapper.updateGroupToDomain(
            updateGroupRequest,
            getMembersWithAdminByListId(updateGroupRequest.getMembers(), currentUser)
    );

    // Save the updated group
    Group savedGroup = groupPort.updateGroup(updateGroupRequest.getId(), updatedGroup);

    return GroupResponse.builder().groupId(savedGroup.getGroupId())
            .groupBackground(savedGroup.getGroupBackground())
            .groupName(savedGroup.getGroupName())
            .groupType(savedGroup.getGroupType())
            .members(savedGroup.getMembers().stream().map(member ->
                            MemberDto.builder()
                                    .userId(member.getUserId())
                                    .nickname(member.getNickname())
                                    .role(member.getRole().toString())
                                    .build())
                    .collect(Collectors.toList()))
            .build();
  }

  @Override
  public MessageResponse deleteGroup(String groupId) {
    User currentUser = authPort.getUserAuth();
    // Validate the group ID
    Group existingGroup = groupPort.getGroup(groupId);
    if (existingGroup == null) {
      throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
    }

    // Check if the current user is an admin of the group
    boolean isAdmin = existingGroup.getMembers().stream()
            .anyMatch(member -> member.getUserId().equals(currentUser.getUserId()) && member.getRole().equals(EMemberRole.ADMIN.toString()));

    // If the user is not an admin, check if there is any admin in the group
    if (!isAdmin) {
      boolean hasAdmin = existingGroup.getMembers().stream()
              .anyMatch(member -> member.getRole().equals(EMemberRole.ADMIN.toString()));

      // If there is no admin, make the first member admin for the purpose of this operation
      if (!hasAdmin) {
        // Make the first member admin
        Member firstMember = existingGroup.getMembers().get(0);
        firstMember.setRole(EMemberRole.ADMIN);

        // Update the group with the new admin
        groupPort.updateGroup(groupId, existingGroup);

        // Now the first member is effectively an admin
        isAdmin = firstMember.getUserId().equals(currentUser.getUserId());
      }
    }
    if (!isAdmin) {
      throw new CustomException("You are not authorized to delete this group", HttpStatus.FORBIDDEN);
    }
        // Delete the group
        groupPort.deleteGroup(groupId);
    return new MessageResponse("Group deleted successfully");
  }

  @Override
  public GroupResponse setMemberRole(ChangeMemberRoleRequest changeMemberRoleRequest) {

    // Lấy thông tin nhóm dựa vào groupId
    Group existingGroup = groupPort.getGroup(changeMemberRoleRequest.getGroupId());
    if (existingGroup == null) {
      throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
    }
    User currentUser = authPort.getUserAuth();
    // Kiểm tra xem người dùng hiện tại có phải là admin của nhóm hay không
    boolean isAdmin = existingGroup.getMembers().stream()
            .anyMatch(member -> member.getUserId().equals(currentUser.getUserId())
                    && member.getRole().equals(EMemberRole.ADMIN.toString()));
    if (!isAdmin) {
      throw new CustomException("You do not have permission to change roles in this group", HttpStatus.FORBIDDEN);
    }
    // Tìm thành viên với userId
    Optional<Member> optionalMember = existingGroup.getMembers().stream()
            .filter(member -> member.getUserId().equals(changeMemberRoleRequest.getUserId()))
            .findFirst();
    // Cập nhật vai trò của thành viên
    Member member = optionalMember.get();
    member.setRole(EMemberRole.valueOf(changeMemberRoleRequest.getNewRole().toString()));
    return groupMapper.groupToResponse( groupPort.setMemberRole(changeMemberRoleRequest.getUserId(), changeMemberRoleRequest.getGroupId(), changeMemberRoleRequest.getNewRole()));
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

  private Member userToMember(User member, EMemberRole role) {
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
              return userToMember(member, EMemberRole.EMPLOYEE);
            }
    ).toList();
  }

  private List<Member> getMembersWithAdminByListId(List<Long> memberIds, User admin) {
    Member adminMember = userToMember(admin, EMemberRole.ADMIN);
    List<Member> members = new ArrayList<>(getMembersByListId(memberIds));
    members.add(adminMember);
    return members;
  }
}
