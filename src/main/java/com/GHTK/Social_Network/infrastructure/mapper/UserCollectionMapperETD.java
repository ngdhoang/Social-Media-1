package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserCollectionMapperETD {
  @Mapping(source = "id", target = "id")
  UserCollectionDomain toDomain(UserCollection userCollection);
}
