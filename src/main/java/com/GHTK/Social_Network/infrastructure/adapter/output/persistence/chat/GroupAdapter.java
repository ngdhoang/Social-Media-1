package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.EMemberRole;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.GroupRepository;
import com.GHTK.Social_Network.infrastructure.mapper.GroupMapperETD;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupAdapter implements GroupPort {
  private final GroupRepository groupRepository;

  private final GroupMapperETD groupMapperETD;

  @Override
  public Group createGroup(Group newGroup) {
    GroupCollection newGroupCollection = groupMapperETD.toEntity(newGroup);
    GroupCollection savedGroupCollection = groupRepository.save(newGroupCollection);
    return groupMapperETD.toDomain(savedGroupCollection);
  }

  @Override
  public Group createGroupPersonal(Long userSendId, Long userReceiveId) {
    String groupName = String.format("%d_%d", userSendId, userReceiveId);

    List<Member> members = Arrays.asList(
            Member.builder().userId(userSendId).build(),
            Member.builder().userId(userReceiveId).build()
    );

    Group newGroup = Group.builder()
            .groupName(groupName)
            .groupType(EGroupType.PERSONAL)
            .members(members)
            .build();
    return createGroup(newGroup);
  }

  @Override
  public Group getGroup(String groupId) {
    Optional<GroupCollection> optionalGroup = groupRepository.findById(groupId);
    return optionalGroup.map(groupMapperETD::toDomain).orElse(null);
  }

  @Override
  public boolean isUserInGroup(Long userId, String groupId) {
    Group group = getGroup(groupId);
    if (group == null) {
      return false;
    }

    return group.getMembers().stream()
            .anyMatch(m -> m.getUserId().equals(userId));
  }

  @Override
  public Group createGroupGroup(Long userSendId, List<Long> userReceiveIds) {
    String groupName = String.format("%d_%s", userSendId, userReceiveIds.toString());

    List<Member> members = new ArrayList<>();
    // Add the sender to the group
    members.add(Member.builder().userId(userSendId).build());

    // Add all the recipients to the group
    for (Long userId : userReceiveIds) {
      members.add(Member.builder().userId(userId).build());
    }

    Group newGroup = Group.builder()
            .groupName(groupName)
            .groupType(EGroupType.GROUP)
            .members(members)
            .build();

    return createGroup(newGroup);
  }

  @Override
  public Group setMemberRole(Long userId, String groupId, EMemberRole newRole) {
    Group group = getGroup(groupId);
    if (group == null) {
      throw new IllegalArgumentException("Group not found");
    }
    // Find the member with the given userId
    Optional<Member> optionalMember = group.getMembers().stream()
            .filter(m -> m.getUserId().equals(userId))
            .findFirst();

    if (!optionalMember.isPresent()) {
      throw new IllegalArgumentException("User is not a member of the group");
    }
    // Update the member's role
    Member member = optionalMember.get();
    member.setRole(com.GHTK.Social_Network.domain.collection.chat.EMemberRole.valueOf(String.valueOf(newRole)));

    // Save the updated group back to the repository
   return createGroup(group);
  }

  @Override
  public Group updateGroup(String groupId,Group updateGroup) {
    Group oldGroup = getGroup(groupId);
    if (oldGroup == null){
      throw new IllegalArgumentException("Group not found");
    }
    if (updateGroup.getGroupName() != null) {
      oldGroup.setGroupName(updateGroup.getGroupName());
    }
    if (updateGroup.getGroupType() != null) {
      oldGroup.setGroupType(updateGroup.getGroupType());
    }
    if (updateGroup.getMembers() != null && !updateGroup.getMembers().isEmpty()) {
      oldGroup.setMembers(updateGroup.getMembers());
    }
    GroupCollection updatedGroupCollection  = groupMapperETD.toEntity(oldGroup);
    GroupCollection savedGroupCollection  = groupRepository.save(updatedGroupCollection);

    return groupMapperETD.toDomain(savedGroupCollection);
  }

  @Override
  public void deleteGroup(String groupId) {

//    // Chuyển đổi groupId từ String sang ObjectId
//    ObjectId groupObjectId = convertStringToObjectId(groupId);

    // Check if the group exists before attempting to delete it
    Optional<GroupCollection> optionalGroup = groupRepository.findById(groupId);

    if (!optionalGroup.isPresent()) {
      throw new IllegalArgumentException("Group not found");
    }

    // Delete the group from the repository
    groupRepository.deleteById(groupId);
  }
//  private ObjectId convertStringToObjectId(String groupId) {
//    try {
//      return new ObjectId(groupId);
//    } catch (IllegalArgumentException e) {
//      throw new IllegalArgumentException("Invalid groupId format", e);
//    }
//  }

}
