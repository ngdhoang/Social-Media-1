package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface GroupMapperETD {
  @Mapping(target = "createAt", ignore = true)
  GroupCollection toEntity(Group group);

  Group toDomain(GroupCollection groupCollection);
}