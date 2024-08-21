package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;

import java.util.List;
import java.util.Set;

public interface GroupPort {
  UserCollectionDomain saveUser(UserCollectionDomain user);

  Group saveGroup(Group newGroup);

  Group createGroupPersonal(Long userSendId, Long userReceiveId);

  Group getGroupForPersonal(String groupName);

  Group getGroupForGroup(String groupId);

  boolean isUserInGroup(Long userId, String groupId);

  List<Group> getMyGroups(Long userId, PaginationRequest paginationRequest);

  UserCollectionDomain getUserGroups(Long userId, int skip, int limit);

  Group createGroupGroup(Long userSendId, List<Long> userReceiveIds);

  Group setMemberRole(Long userId, String groupId, EStateUserGroup newRole);

  Set<Group> getGroupsByUserId(Long userId);

  void removeMemberByUserId(String groupId, Long userId);

  Member getMemberByUserId(String groupId, Long userId);

  void addMember(String groupId, Member newMember);
}
