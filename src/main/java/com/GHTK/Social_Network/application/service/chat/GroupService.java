package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.OfflineOnlineInput;
import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.OfflineOnlinePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.MessagePort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.domain.collection.chat.*;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.GroupMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.MemberDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetMemBerNickNameRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.CreateGroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final WebsocketClientPort websocketClientPort;
    private final MessagePort messagePort;
    private final OfflineOnlineInput offlineOnlineInput;
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
        CreateGroupResponse createGroupResponse = groupMapper.groupToResponse(groupPort.saveGroup(newGroup));
        setMemberInGroup(members, createGroupResponse.getGroupId());

        Message message = Message.builder()
                .userAuthId(currentUser.getUserId())
                .content(currentUser.getUserEmail()+ " đã tạo nhóm này")
                .msgType(EMessageType.MESSAGE)

        .build();

        websocketClientPort.sendListUserAndSave(message,members.stream().map(
                Member::getUserId
        ).toList());
        return createGroupResponse;
    }

    @Override
    public CreateGroupResponse updateGroup(UpdateGroupRequest updateGroupRequest) {
        User currentUser = authPort.getUserAuth();

        // Validate the group ID
        Group existingGroup = groupPort.getGroupForGroup(updateGroupRequest.getId());
        if (existingGroup == null) {
            throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
        }

        //Check FriendShip
        validateFriendship(currentUser.getUserId(), updateGroupRequest.getMembers());

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
            return CreateGroupResponse.builder().groupId(savedGroup.getId())
                    .groupBackground(savedGroup.getGroupBackground())
                    .groupName(savedGroup.getGroupName())
                    .groupType(savedGroup.getGroupType().toString())
                    .members(savedGroup.getMembers().stream().map(member ->
                                    MemberDto.builder()
                                            .userId(member.getUserId())
                                            .nickname(member.getNickname())
                                            .role(member.getRole().toString())
                                            .build())
                            .collect(Collectors.toList()))
                    .createAt(LocalDate.now())
                    .build();
    }

    @Override
    public MessageResponse deleteGroup(String groupId) {
        User currentUser = authPort.getUserAuth();
        // Validate the group ID
        Group existingGroup = groupPort.getGroupForGroup(groupId);
        if (existingGroup == null) {
            throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
        }

        // Check if the current user is an admin of the group
        boolean isAdmin = existingGroup.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(currentUser.getUserId()) && member.getRole().equals(EStateUserGroup.ADMIN));
        if (!isAdmin) {
            throw new CustomException("You are not authorized to delete this group", HttpStatus.FORBIDDEN);
        }
        // Delete the group
        groupPort.deleteGroup(groupId);
        return new MessageResponse("Group deleted successfully");
    }

    @Override
    public CreateGroupResponse addNewMemberToGroup(String groupId, List<Member> members) {
        User currentUser = authPort.getUserAuth();

        // Validate the group ID
        Group existingGroup = groupPort.getGroupForGroup(groupId);
        if (existingGroup == null) {
            throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
        }

        // Check if the current user is a member of the group
        boolean isMember = existingGroup.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(currentUser.getUserId()));

        if (!isMember) {
            throw new CustomException("You are not a member of this group and cannot update it", HttpStatus.FORBIDDEN);
        }

        // Ensure all new members are friends with the current user
        validateFriendship(currentUser.getUserId(), members.stream()
                .map(Member::getUserId)
                .collect(Collectors.toList()));

        // Add new members to the group if they are not already part of it
        for (Member newMember : members) {
            boolean isExistingMember = existingGroup.getMembers().stream()
                    .anyMatch(member -> member.getUserId().equals(newMember.getUserId()));

            if (!isExistingMember) {
                existingGroup.getMembers().add(newMember);
            } else {
                throw new CustomException("Some of the users are already in the group", HttpStatus.CONFLICT);
            }
        }
        // Update group details
        existingGroup.setMembers(members);

        // Save the updated group
        Group savedGroup = groupPort.updateGroup(groupId, existingGroup);

        return CreateGroupResponse.builder()
                .groupId(savedGroup.getId())
                .groupBackground(savedGroup.getGroupBackground())
                .groupName(savedGroup.getGroupName())
                .groupType(savedGroup.getGroupType().toString())
                .members(savedGroup.getMembers().stream().map(member ->
                                MemberDto.builder()
                                        .userId(member.getUserId())
                                        .nickname(member.getNickname())
                                        .role(member.getRole().toString())
                                        .build())
                        .collect(Collectors.toList()))
                .createAt(LocalDate.now())
                .build();
    }

    @Override
    public MessageResponse outGroup(String groupId) {
        User currentUser = authPort.getUserAuth();
        Group group = getGroup(groupId);
        if (group == null) {
            throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
        }
        // Check if the current user is a member of the group
        boolean isMember = group.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(currentUser.getUserId()));
        if (!isMember) {
            throw new CustomException("You are not a member of this group and cannot out group", HttpStatus.FORBIDDEN);
        }

        //Set lastGroupName = GroupName
        // Find the corresponding UserGroup in the user's collection
        // Retrieve UserCollectionDomain
        UserCollectionDomain userCollection = authPort.getUserCollectionById(currentUser.getUserId());
        UserGroup userGroup = userCollection.getUserGroupInfoList().stream()
                .filter(ug -> ug.getGroupId().equals(groupId))
                .findFirst()
                .orElse(null);

        userGroup.setLastGroupName(group.getGroupName());
        // Save the updated group
        groupPort.updateGroup(groupId, deleteMemberInGroup(groupId, currentUser.getUserId()));
        // Thong bao vao group thanh vien đã out nhóm.
        return new MessageResponse("Out group successful!");
    }

    @Override
    public MessageResponse setMemberNickName(SetMemBerNickNameRequest setMemBerNickNameRequest) {
        User currentUser = authPort.getUserAuth();
        Group group = getGroup(setMemBerNickNameRequest.getGroupId());
        validateGroupForMe(group, currentUser.getUserId());
        Optional<Member> optionalMember = group.getMembers().stream()
            .filter(member -> member.getUserId().equals(setMemBerNickNameRequest.getUserId()))
            .findFirst();
        Member member = optionalMember.get();
        member.setNickname(setMemBerNickNameRequest.getNickName());
        groupPort.updateGroup(group.getId(), group);
        //save new member
        System.out.println(member);
        return new  MessageResponse("Set nickname for user " + setMemBerNickNameRequest.getUserId() + " successful !") ;
    }

    @Override
    public List<GroupResponse> getAllMyGroups( PaginationRequest paginationRequest) {
        User currentUser = authPort.getUserAuth();

        UserCollectionDomain userGroupInfoList = groupPort.findUserListGroupWithPagination(
                currentUser.getUserId(),
                paginationRequest.getPage(),
                paginationRequest.getSize()
        );
        System.out.println(  groupInfoToResponse(userGroupInfoList.getUserGroupInfoList().get(0), currentUser.getUserId()));
//        List<GroupResponse> groupResponses = userGroupInfoList.getUserGroupInfoList().stream()
//                .map(userGroup -> groupInfoToResponse(userGroup, currentUser.getUserId()))
//                .collect(Collectors.toList());
        return null;
    }

    private GroupResponse groupInfoToResponse(UserGroup userGroup, Long currentId ){
        Group group = getGroup(userGroup.getGroupId());
        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setGroupId(userGroup.getGroupId());
        groupResponse.setGroupName(
                userGroup.getLastGroupName() != null ? group.getGroupName() : userGroup.getLastGroupName()
        );
//        groupResponse.setLastMessage(messagePort.getMessageById(userGroup.getLastMsgId()).getContent());
        groupResponse.setGroupBackground(group.getGroupBackground());
       if(group.getGroupType().equals(EGroupType.PERSONAL)){
           Long userId = getUserInGroupPersonal(group.getMembers(),currentId );
           groupResponse.setOnline(offlineOnlineInput.isUserOnline(userId));
           UserCollectionDomain userCollectionDomain = authPort.getUserCollectionById(userId);
           groupResponse.setLastActive(userCollectionDomain.getLastActive());
       }
        return groupResponse;
    }

    private Long getUserInGroupPersonal(List<Member> members, Long currentId ){
        return members.get(0).getUserId().equals(currentId) ? members.get(1).getUserId() : members.get(0).getUserId();
    }
//        List<Group> groups = groupPort.getPageMyGroups(userId, paginationRequest );
//      return groups.stream().map(
//              g -> {
//                  GroupResponse groupResponse = new GroupResponse();
//                  groupResponse.setGroupId(g.getId());
//                  groupResponse.setGroupName(g.getGroupName());
//                  groupResponse.setGroupType(g.getGroupType().toString());
//
//                  if (g.getGroupType().equals(EGroupType.PERSONAL) && g.getMembers().size() == 2) {
//                      List<Member> members = g.getMembers();
//                      Member otherMember = members.stream()
//                              .filter(member -> !member.getUserId().equals(userId))
//                              .findFirst()
//                              .orElse(null);
//
//                      if (otherMember != null) {
//                          otherMember.getUserId();
//                          groupResponse.(otherMember.);
//                      }
//                  }
//              }



//      @Override
//  public GroupResponse setMemberRole(ChangeMemberRoleRequest changeMemberRoleRequest) {
//
//    // Lấy thông tin nhóm dựa vào groupId
//    Group existingGroup = groupPort.getGroup(changeMemberRoleRequest.getGroupId());
//    if (existingGroup == null) {
//      throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
//    }
//    User currentUser = authPort.getUserAuth();
//    // Kiểm tra xem người dùng hiện tại có phải là admin của nhóm hay không
//    boolean isAdmin = existingGroup.getMembers().stream()
//            .anyMatch(member -> member.getUserId().equals(currentUser.getUserId())
//                    && member.getRole().equals(EMemberRole.ADMIN.toString()));
//    if (!isAdmin) {
//      throw new CustomException("You do not have permission to change roles in this group", HttpStatus.FORBIDDEN);
//    }
//    // Tìm thành viên với userId
//    Optional<Member> optionalMember = existingGroup.getMembers().stream()
//            .filter(member -> member.getUserId().equals(changeMemberRoleRequest.getUserId()))
//            .findFirst();
//    // Cập nhật vai trò của thành viên
//    Member member = optionalMember.get();
//    member.setRole(EMemberRole.valueOf(changeMemberRoleRequest.getNewRole().toString()));
//    return groupMapper.groupToResponse( groupPort.setMemberRole(changeMemberRoleRequest.getUserId(), changeMemberRoleRequest.getGroupId(), changeMemberRoleRequest.getNewRole()));
//  }

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
                            EStateUserGroup.USER
                    ));
                    groupPort.saveUser(userCollectionDomain);
                }
        );
    }

    private void validateGroupForMe(Group group, Long userId) {
        if (group == null){
            throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
        }
        if (!groupPort.isUserInGroup(userId, group.getId())){
            throw new CustomException("You are not a member of this group", HttpStatus.FORBIDDEN);
        }
    }

    private List<Long> getGroupMemberIds(List<Member> members) {
        return members.stream().map(Member::getUserId).toList();
    }

    private Group deleteMemberInGroup(String groupId, Long userId){
        Group group = getGroup(groupId);
        List<Member> members =  group.getMembers().stream()
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
