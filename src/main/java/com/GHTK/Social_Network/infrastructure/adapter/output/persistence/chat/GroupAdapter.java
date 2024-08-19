package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.EStateUserGroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MemberCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.GroupRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import com.GHTK.Social_Network.infrastructure.mapper.GroupMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.UserCollectionMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupAdapter implements GroupPort {
  private final MongoTemplate mongoTemplate;
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
    System.out.println(newGroup);
    GroupCollection newGroupCollection = groupMapperETD.toEntity(newGroup);
    System.out.println(newGroupCollection);
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
      System.out.println(groupId);
    Optional<GroupCollection> optionalGroup = groupRepository.findById(groupId);
    return optionalGroup.map(groupMapperETD::toDomain).orElse(null);
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

  @Override
  public List<Group> getPageMyGroups(Long userId, PaginationRequest paginationRequest) {
    Pageable pageable = paginationRequest.toPageable();
    return groupRepository.findAllByUserId(userId, pageable).stream().map(
                    g -> {

                      // Nếu không phải nhóm cá nhân, trả về nhóm bình thường
                      return groupMapperETD.toDomain(g);
                    }
            )
            .toList();
  }

  @Override
  public UserCollectionDomain findUserListGroupWithPagination(Long userId, int skip, int limit) {
    Query query = new Query(Criteria.where("userId").is(userId));
    query.fields().include("userGroupInfoList").slice("userGroupInfoList", skip, limit);
    UserCollection result = mongoTemplate.findOne(query, UserCollection.class);
    return userCollectionMapperETD.toDomain(result);
  }

//  @Override
//  public void outGroup(String groupId) {
//    Group group = getGroupForGroup(groupId);
//    if (group == null){
//      return false;
//    }
//
//  }

  private void setMemberInUser(Long userReceiveId, String groupName) {
    UserCollection userReceive = userCollectionRepository.findByUserId(userReceiveId);
    if (userReceive.getUserGroupInfoList() == null) {
      userReceive.setUserGroupInfoList(new ArrayList<>());
    }
    userReceive.getUserGroupInfoList().add(
            new UserGroupInfo(groupName, EStateUserGroupCollection.USER)
    );
    userCollectionRepository.save(userReceive);
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
            .groupType(EGroupType.GROUP_PUBLIC)
            .members(members)
            .build();

    return saveGroup(newGroup);
  }

  @Override
  public Group setMemberRole(Long userId, String groupId, EStateUserGroup newRole) {
    Group group = getGroupForGroup(groupId);
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
    member.setRole(EStateUserGroup.valueOf(newRole.toString()));

    // Save the updated group back to the repository
    return saveGroup(group);
  }

  @Override
  public Group updateGroup(String groupId,Group updateGroup) {
    Group oldGroup = getGroupForGroup(groupId);
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

  }

  private Instant getLastActive(Long userId){
    UserCollection user = userCollectionRepository.findByUserId(userId);
    Instant t =  user.getLastActive();
    return t;
  }
}
