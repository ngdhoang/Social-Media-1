package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserCollectionMapperETD {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "userGroupInfoList", target = "userGroupInfoList", qualifiedByName = "map")
  UserCollectionDomain toDomain(UserCollection userCollection);

  @Named("map")
  default UserGroup toUserGroupInfor(UserGroupInfo userGroupInfo) {
    return null;
  }
}
