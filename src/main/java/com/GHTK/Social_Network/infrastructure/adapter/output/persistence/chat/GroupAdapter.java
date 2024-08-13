package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.GroupRepository;
import com.GHTK.Social_Network.infrastructure.mapper.GroupMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    String groupName = userSendId < userReceiveId
            ? String.format("%d_%d", userSendId, userReceiveId)
            : String.format("%d_%d", userReceiveId, userSendId);

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
  public Group getGroupForPersonal(String groupName) {
    Optional<GroupCollection> optionalGroup = groupRepository.findByGroupName(groupName);
    return optionalGroup.map(groupMapperETD::toDomain).orElse(null);
  }

  @Override
  public Group getGroupForGroup(String groupId) {
    Optional<GroupCollection> optionalGroup = groupRepository.findById(groupId);
    return optionalGroup.map(groupMapperETD::toDomain).orElse(null);
  }

  @Override
  public boolean isUserInGroup(Long userId, String groupId) {
    Group group = getGroupForPersonal(groupId);
    if (group == null) {
      return false;
    }

    return group.getMembers().stream()
            .anyMatch(m -> m.getUserId().equals(userId));
  }
}
