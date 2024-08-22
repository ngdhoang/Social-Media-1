package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.application.port.input.chat.OfflineOnlineInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.UserCollectionPort;
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
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.MemberRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.SetMemBerNickNameRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.CreateGroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService implements GroupPortInput {
    private final OfflineOnlineInput offlineOnlineInput;

    private final FriendShipPort friendShipPort;
    private final GroupPort groupPort;
    private final AuthPort authPort;
    private final WebsocketClientPort websocketClientPort;
    private final CloudPort cloudPort;
    private final UserCollectionPort userCollectionPort;

    private final GroupMapper groupMapper;

    @Override
    public CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest) {
        User currentUser = authPort.getUserAuth();

        validateFriendship(currentUser.getUserId(), createGroupRequest.getMembers());
        List<Member> members = getMembersWithAdminByListId(createGroupRequest.getMembers(), currentUser);
        Group newGroup = groupMapper.createGroupToDomain(
                createGroupRequest
        );
        newGroup.setGroupType(EGroupType.valueOf(createGroupRequest.getGroupType()));
        newGroup.setMembers(members);
        CreateGroupResponse createGroupResponse = groupMapper.groupToResponse(groupPort.saveGroup(newGroup));
        setMemberInGroup(members, createGroupResponse.getGroup().getGroupId());

        sendNotificationsForGroup(
                currentUser,
                currentUser.getUserEmail() + " created group",
                newGroup
        );

        return createGroupResponse;
    }

    @Override
    public CreateGroupResponse changeGroupName(UpdateGroupRequest updateGroupRequest) {
        User currentUser = authPort.getUserAuth();
        Group group = getGroup(updateGroupRequest.getGroupId());

        validateGroupForMe(group, currentUser.getUserId());
        validatePermissionGroup(group, currentUser.getUserId());

        group.setGroupName(updateGroupRequest.getGroupName());

        sendNotificationsForGroup(
                currentUser,
                currentUser.getUserEmail() + " change group name",
                group
        );

        return groupMapper.groupToResponse(groupPort.saveGroup(group));
    }

    @Override
    public CreateGroupResponse changeGroupBackground(MultipartFile backgroundImage, String groupId) {
        User currentUser = authPort.getUserAuth();
        Group group = getGroup(groupId);

        validateGroupForMe(group, currentUser.getUserId());
        validatePermissionGroup(group, currentUser.getUserId());

        String url = (String) cloudPort.uploadPictureByFile(backgroundImage, ImageHandlerPortInput.MAX_SIZE_AVATAR).get("url");
        group.setGroupBackground(url);

        sendNotificationsForGroup(
                currentUser,
                currentUser.getUserEmail() + " change group background",
                group

        );

        return groupMapper.groupToResponse(groupPort.saveGroup(group));
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

        memberRequest.getMemberId().forEach(id -> {
            User newMember = authPort.getUserById(id);

            sendNotificationsForGroup(currentUser,
                    currentUser.getUserEmail() + " added " + newMember.getUserEmail()
                    , group);
        });

        return groupMapper.groupToResponse(getGroup(memberRequest.getGroupId()));
    }


    @Override
    public CreateGroupResponse kickMemberToGroup(MemberRequest memberRequest) {
        User currentUser = authPort.getUserAuth();
        Group group = getGroup(memberRequest.getGroupId());

        validateGroupForMe(group, currentUser.getUserId());
        validatePermissionGroup(group, currentUser.getUserId());

        Member currentMember = groupPort.getMemberByUserId(group.getId(), currentUser.getUserId());
        if(!currentMember.getRole().equals(EStateUserGroup.ADMIN)) {
            throw new CustomException("User cannot kick members in group", HttpStatus.UNAUTHORIZED);
        }

        List<Long> existingMemberIds = group.getMembers().stream()
                .map(Member::getUserId)
                .toList();
        List<Long> membersToRemove = memberRequest.getMemberId().stream()
                .filter(existingMemberIds::contains)
                .toList();

        if (membersToRemove.isEmpty()) {
            throw new IllegalArgumentException("No valid members found to remove from the group");
        }

        membersToRemove.forEach(memberId -> {
            User user = authPort.getUserById(memberId);

            sendNotificationsForGroup(
                    currentUser,
                    currentUser.getUserEmail() + " kick " + user.getUserEmail(),
                    group

            );

            groupPort.removeMemberByUserId(memberRequest.getGroupId(), memberId);
        });

        Group updatedGroup = getGroup(memberRequest.getGroupId());

        return groupMapper.groupToResponse(updatedGroup);
    }

    @Override
    public MessageResponse outGroup(String groupId) {
        User currentUser = authPort.getUserAuth();
        Group group = getGroup(groupId);

        validateGroupForMe(group, currentUser.getUserId());

        if (!groupPort.isUserInGroup(currentUser.getUserId(), groupId)) {
            throw new CustomException("You are not a member of this group and cannot out group", HttpStatus.FORBIDDEN);
        }

        groupPort.removeMemberByUserId(groupId, currentUser.getUserId());

        if (groupPort.getLastMember(groupId) != null) {
            groupPort.setMemberRole(groupPort.getLastMember(groupId).getUserId(), groupId, EStateUserGroup.ADMIN);
        }
        System.out.println(groupPort.getLastMember(groupId));
        UserGroup userGroup = userCollectionPort.getUserGroupByUserId(currentUser.getUserId(), groupId);
        userGroup.setLastGroupName(group.getGroupName());
        userCollectionPort.addUserGroup(
                currentUser.getUserId(),
                userGroup
        );

        sendNotificationsForGroup(
                currentUser,
                currentUser.getUserEmail() + " out group",
                group

        );

        return new MessageResponse("Out group successful!");
    }

    @Override
    public MessageResponse changeNickname(SetMemBerNickNameRequest setMemBerNickNameRequest) {
        User currentUser = authPort.getUserAuth();
        Group group = getGroup(setMemBerNickNameRequest.getGroupId());

        validateGroupForMe(group, currentUser.getUserId());
        validatePermissionGroup(group, currentUser.getUserId());

        Member member = groupPort.getMemberByUserId(group.getId(), setMemBerNickNameRequest.getUserId());
        member.setNickname(setMemBerNickNameRequest.getNickName());
        groupPort.removeMemberByUserId(group.getId(), member.getUserId());
        groupPort.addMember(group.getId(), member);

        sendNotificationsForGroup(
                currentUser,
                currentUser.getUserEmail() + " change nick name to: " + setMemBerNickNameRequest.getNickName(),
                group
        );

        return new MessageResponse("Change nickname successful!");
    }

    @Override
    public MessageResponse changeStateGroup(String groupId, String state) {
        User currentUser = authPort.getUserAuth();
        Group group = getGroup(groupId);

        if (group.getGroupType().equals(EGroupType.PERSONAL)) {
            throw new CustomException("User cannot change state group", HttpStatus.UNAUTHORIZED);
        }

        validateGroupForMe(group, currentUser.getUserId());
        validatePermissionGroup(group, currentUser.getUserId());

        Member currentMember = groupPort.getMemberByUserId(groupId, currentUser.getUserId());
        if(!currentMember.getRole().equals(EStateUserGroup.ADMIN)) {
            throw new CustomException("User cannot change state group", HttpStatus.UNAUTHORIZED);
        }

        if (state.equals("GROUP_PRIVATE")) {
            group.setGroupType(EGroupType.GROUP_PRIVATE);
        } else {
            group.setGroupType(EGroupType.GROUP_PUBLIC);
        }
        groupPort.saveGroup(group);

        sendNotificationsForGroup(
                currentUser,
                currentUser.getUserEmail() + " change state group to " + group.getGroupType(),
                group

        );

        return new MessageResponse("State group change successful !");
    }

    @Override
    public List<GroupResponse> getMyGroups(PaginationRequest paginationRequest) {
        User currentUser = authPort.getUserAuth();

        UserCollectionDomain userGroupInfoList = groupPort.getUserGroups(
                currentUser.getUserId(),
                paginationRequest.getPage(),
                paginationRequest.getSize()
        );
        return userGroupInfoList.getUserGroupInfoList().stream()
                .map(userGroup ->
                        groupInfoToResponse(userGroup, currentUser.getUserId()))
                .toList();
    }

    private GroupResponse groupInfoToResponse(UserGroup userGroup, Long currentId) {
        Group group = getGroup(userGroup.getGroupId());
        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setGroupId(userGroup.getGroupId());
        groupResponse.setGroupName(
                userGroup.getLastGroupName() == null ? group.getGroupName() : userGroup.getLastGroupName()
        );
        groupResponse.setGroupBackground(group.getGroupBackground());
        if (group.getGroupType().equals(EGroupType.PERSONAL)) {
            Long userId = getUserInGroupPersonal(group.getMembers(), currentId);
            groupResponse.setOnline(offlineOnlineInput.isUserOnline(userId));
            UserCollectionDomain userCollectionDomain = authPort.getUserCollectionById(userId);
            groupResponse.setLastActive(userCollectionDomain.getLastActive());
        }
        System.out.println(group.getGroupType());
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
                throw new CustomException("UserId " + userId + " don't exist", HttpStatus.BAD_REQUEST);
            }
        });
    }


    private void addNewMembersToGroup(Group group, List<Long> newMemberIds, User currentUser, List<Long> existingMemberIds) {
        newMemberIds.forEach(id -> {
            groupPort.addMember(group.getId(),
                    Member.builder()
                            .userId(id)
                            .role(EStateUserGroup.USER)
                            .build()
            );


            userCollectionPort.removeUserGroup(id, group.getId());

            userCollectionPort.addUserGroup(id,
                    UserGroup.builder()
                            .groupId(group.getId())
                            .build()
            );
        });
    }

    private void sendNotificationsForGroup(User currentUser, String content, Group group) {
        Message message = websocketClientPort.createNotificationMessage(
                currentUser.getUserId(),
                content
        );
        websocketClientPort.sendListUserAndSave(message,
                group.getMembers().stream().map(
                        Member::getUserId
                ).toList());
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
        return Member.builder()
                .userId(member.getUserId())
                .role(newRole)
                .build();
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
        return EGroupType.isGroupPublic(group.getGroupType());
    }

    private boolean isUserAuthorized(Group group, Long userId) {
        return group.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(userId) &&
                        !member.getRole().equals(EStateUserGroup.USER));
    }

    private Group getGroup(String groupId) {
        boolean isGroupPersonal = groupId.contains("_");
        return isGroupPersonal ? groupPort.getGroupForPersonal(groupId) : groupPort.getGroupForGroup(groupId);
    }

}
