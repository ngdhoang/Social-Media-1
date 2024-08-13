package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.EGroupTypeCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.GroupCollection;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface GroupMapperETD {
  @Mapping(target = "groupId", source = "groupId",qualifiedByName = "stringToObjectId")
  @Mapping(target = "groupType", source = "groupType",qualifiedByName = "domainToEntity")
  GroupCollection toEntity(Group group);

  @Mapping(target = "groupId", source = "groupId",qualifiedByName = "objectIdToString")
  @Mapping(target = "groupType", source = "groupType",qualifiedByName = "entityToDomain")
  Group toDomain(GroupCollection groupCollection);

  @Named("entityToDomain")
  default EGroupType entityToDomain(EGroupTypeCollection groupType) {
    return EGroupType.valueOf(groupType.toString());
  }
  @Named("domainToEntity")
  default EGroupTypeCollection domainToEntity(EGroupType groupType) {
    return EGroupTypeCollection.valueOf(groupType.toString());
  }

  @Named("stringToObjectId")
  default ObjectId stringToObjectId(String id) {
    return id != null ? new ObjectId(id) : null;
  }

  @Named("objectIdToString")
  default String objectIdToString(ObjectId id) {
    return id != null ? id.toString() : null;
  }
}