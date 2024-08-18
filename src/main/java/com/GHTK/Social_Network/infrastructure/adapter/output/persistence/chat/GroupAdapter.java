package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.EStateUserGroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.GroupRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import com.GHTK.Social_Network.infrastructure.mapper.GroupMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.UserCollectionMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupAdapter implements GroupPort {
  private final GroupRepository groupRepository;
  private final UserCollectionRepository userRepository;

  private final GroupMapperETD groupMapperETD;
  private final UserCollectionMapperETD userCollectionMapperETD;
  private final UserCollectionRepository userCollectionRepository;

  @Override
  public UserCollectionDomain saveUser(UserCollectionDomain user) {
    UserCollection u = userRepository.findByUserId(user.getUserId());
    user.setId(u.getId());
    return userCollectionMapperETD.toDomain(
            userRepository.save(userCollectionMapperETD.toEntity(user))
    );
  }

  @Override
  public Group saveGroup(Group newGroup) {
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

    setMemberInUser(userSendId, groupName);
    setMemberInUser(userReceiveId, groupName);

    Group newGroup = Group.builder()
            .groupName(groupName)
            .groupType(EGroupType.PERSONAL)
            .members(members)
            .build();

    return saveGroup(newGroup);
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
  public Set<Group> getGroupsByUserId(Long userId) {
    UserCollection userCollection = userCollectionRepository.findByUserId(userId);
    return userCollection.getUserGroupInfoList().stream().map(
            userGroupInfo -> {
              if (userGroupInfo.getState().equals(EStateUserGroupCollection.PERSONAL))
                return getGroupForPersonal(userGroupInfo.getGroupId());
              else
                return getGroupForGroup(userGroupInfo.getGroupId());
            }
    ).collect(Collectors.toSet());
  }

  @Override
  public boolean isUserInGroup(Long userId, String groupId) {
    Group group = getGroupForPersonal(groupId);
    if (group == null) {
      group = getGroupForGroup(groupId);
    }

    return group.getMembers().stream()
            .anyMatch(m -> m.getUserId().equals(userId));
  }

  private void setMemberInUser(Long userReceiveId, String groupName) {
    UserCollection userReceive = userCollectionRepository.findByUserId(userReceiveId);
    if (userReceive.getUserGroupInfoList() == null) {
      userReceive.setUserGroupInfoList(new ArrayList<>());
    }
    userReceive.getUserGroupInfoList().add(
            new UserGroupInfo(groupName, EStateUserGroupCollection.PERSONAL)
    );
    userCollectionRepository.save(userReceive);
  }
}
