package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.payload.dto.MemberDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.CreateGroupResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GroupMapper {
  @Mapping(target = "groupId", source = "id")
  @Mapping(target = "groupType", source = "groupType", qualifiedByName = "mapGroupType")
  @Mapping(target = "msgPin", source = "msgPin")
  @Mapping(target = "members", source = "members", qualifiedByName = "mapMembers")
  @Mapping(target = "createAt", expression = "java(java.time.LocalDate.now())")
  CreateGroupResponse groupToCreateResponse(Group group);


  @Mapping(target = "groupId", source = "id")
  GroupResponse groupToNResponse(Group group);


  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "lastMsgSeen", source = "lastMsgSeen")
  @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
  MemberDto mapMember(Member member);

  @Named("mapGroupType")
  default String mapGroupType(EGroupType groupType) {
    return groupType != null ? groupType.name() : null;
  }

  @Named("mapMembers")
  default List<MemberDto> mapMembers(List<Member> members) {
    if (members == null) {
      return null;
    }
    return members.stream()
            .map(this::mapMember)
            .collect(Collectors.toList());
  }

  @Named("mapRole")
  default String mapRole(EStateUserGroup role) {
    return role != null ? role.name() : null;
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "groupBackground", ignore = true)
  @Mapping(target = "msgPin", ignore = true)
  @Mapping(target = "members", ignore = true)
  Group createGroupToDomain(CreateGroupRequest createGroupRequest);

  @AfterMapping
  default void setMembers(@MappingTarget Group group, @Context List<Member> members) {
    group.setMembers(members);
  }

  default Group updateGroupToDomain(UpdateGroupRequest updateGroupRequest, List<Member> members) {
    return Group.builder()
            .id(updateGroupRequest.getGroupId())
            .groupName(updateGroupRequest.getGroupName())
            .members(members)
            .build();
  }
}


