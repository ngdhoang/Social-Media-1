package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.payload.dto.MemberDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GroupMapper {
  @Mapping(target = "groupId", source = "groupId",qualifiedByName = "mapGroupId")
  @Mapping(target = "groupType", source = "groupType")
  @Mapping(target = "msgPin", source = "msgPin", qualifiedByName = "mapMsgPin")
  @Mapping(target = "createAt", ignore = true)
  GroupResponse groupToResponse(Group group);

  @Mapping(target = "userId", source = "userId", qualifiedByName = "mapUserId")
  @Mapping(target = "lastMsgSeen", source = "lastMsgSeen", qualifiedByName = "mapLastMsgSeen")
  MemberDto mapMember(Member member);

  @Named("mapGroupId")
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
  // Phương thức ánh xạ tùy chỉnh từ ObjectId sang String
  @Named("mapGroupId")
  default String map(ObjectId value) {
    return value != null ? value.toString() : null;
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

  default Group updateGroupToDomain(UpdateGroupRequest updateGroupRequest, List<Member> members) {
    return Group.builder()
            .groupId(updateGroupRequest.getId())
            .groupName(updateGroupRequest.getGroupName())
            .groupType(updateGroupRequest.getGroupType())
            .members(members)
            .build();
  }
}