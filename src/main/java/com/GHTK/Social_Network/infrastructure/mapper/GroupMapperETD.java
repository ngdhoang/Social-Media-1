package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface GroupMapperETD {

  @Mapping(target = "id", source = "id", qualifiedByName = "stringToObjectId")
  @Mapping(target = "createAt", ignore = true)
  GroupCollection toEntity(Group group);

  @Mapping(target = "id", source = "id", qualifiedByName = "objectIdToString")
  Group toDomain(GroupCollection groupCollection);

  @Named("stringToObjectId")
  default ObjectId stringToObjectId(String id) {
    return id == null ? null : new ObjectId(id);
  }

  @Named("objectIdToString")
  default String objectIdToString(ObjectId id) {
    return id == null ? null : id.toHexString();
  }
}