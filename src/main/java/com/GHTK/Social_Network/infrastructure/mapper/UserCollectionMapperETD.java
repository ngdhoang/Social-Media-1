package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserCollectionMapperETD {
  @Mapping(target = "userGroupInfoList", source = "userGroupInfoList")
  UserCollectionDomain toDomain(UserCollection userCollection);

  @Mapping(target = "userGroupInfoList", source = "userGroupInfoList")
  UserCollection toEntity(UserCollectionDomain userCollectionDomain);

  UserGroup userGroupInfoToUserGroup(UserGroupInfo userGroupInfo);

  UserGroupInfo userGroupToUserGroupInfo(UserGroup userGroup);

  List<UserGroup> userGroupInfoListToUserGroupList(List<UserGroupInfo> userGroupInfoList);

  List<UserGroupInfo> userGroupListToUserGroupInfoList(List<UserGroup> userGroupList);
}