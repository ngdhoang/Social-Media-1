package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.payload.dto.MemberDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GroupMapper {
  @Mapping(target = "groupId", source = "groupId")
  @Mapping(target = "groupType", source = "groupType", qualifiedByName = "mapGroupType")
  @Mapping(target = "msgPin", source = "msgPin", qualifiedByName = "mapMsgPin")
  @Mapping(target = "createAt", ignore = true)
  GroupResponse groupToResponse(Group group);

  @Mapping(target = "userId", source = "userId", qualifiedByName = "mapUserId")
  @Mapping(target = "lastMsgSeen", source = "lastMsgSeen", qualifiedByName = "mapLastMsgSeen")
  MemberDto mapMember(Member member);

  @Named("mapGroupType")
  default String mapGroupType(EGroupType groupType) {
    return groupType != null ? groupType.name() : null;
  }

  @Named("mapMsgPin")
  default List<Long> mapMsgPin(List<String> msgPin) {
    if (msgPin == null) {
      return new ArrayList<>();
    }
    return msgPin.stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());
  }

  @Named("mapUserId")
  default Long mapUserId(String userId) {
    return userId != null ? Long.parseLong(userId) : null;
  }

  @Named("mapLastMsgSeen")
  default Long mapLastMsgSeen(String lastMsgSeen) {
    return lastMsgSeen != null ? Long.parseLong(lastMsgSeen) : null;
  }

  default Group createGroupToDomain(CreateGroupRequest createGroupRequest, List<Member> members) {
    return Group.builder()
            .groupName(createGroupRequest.getGroupName())
            .groupType(createGroupRequest.getGroupType())
            .members(members)
            .build();
  }
}