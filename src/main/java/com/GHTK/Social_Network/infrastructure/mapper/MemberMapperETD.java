package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.EStateUserGroupCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MemberCollection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapperETD {
  default Member toDomain(MemberCollection memberCollection) {
    if (memberCollection == null) {
      return null;
    }
    return Member.builder()
            .userId(memberCollection.getUserId())
            .nickname(memberCollection.getNickname())
            .lastTimeMsgSeen(memberCollection.getLastTimeMsgSeen())
            .lastMsgSeen(memberCollection.getLastMsgSeen())
            .role(stateToDomain(memberCollection.getRole()))
            .build();
  }

  default MemberCollection toEntity(Member member) {
    if (member == null) {
      return null;
    }
    return MemberCollection.builder()
            .userId(member.getUserId())
            .nickname(member.getNickname())
            .lastTimeMsgSeen(member.getLastTimeMsgSeen())
            .lastMsgSeen(member.getLastMsgSeen())
            .role(stateToEntity(member.getRole()))
            .build();
  }

  EStateUserGroup stateToDomain(EStateUserGroupCollection eStateUserGroupCollection);

  EStateUserGroupCollection stateToEntity(EStateUserGroup eStateUserGroup);
}
