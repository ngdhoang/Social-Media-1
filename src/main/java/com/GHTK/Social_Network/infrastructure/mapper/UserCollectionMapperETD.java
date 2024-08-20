package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserGroupInfo;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.EStateUserGroupCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserCollectionMapperETD {
  @Mapping(target = "userGroupInfoList", source = "userGroupInfoList")
  UserCollectionDomain toDomain(UserCollection userCollection);

  @Mapping(target = "userGroupInfoList", source = "userGroupInfoList", qualifiedByName = "mapUserGroupInfoList" )
  UserCollection toEntity(UserCollectionDomain userCollectionDomain);

  UserGroup userGroupInfoToUserGroup(UserGroupInfo userGroupInfo);

  UserGroupInfo userGroupToUserGroupInfo(UserGroup userGroup);

  List<UserGroup> userGroupInfoListToUserGroupList(List<UserGroupInfo> userGroupInfoList);

  List<UserGroupInfo> userGroupListToUserGroupInfoList(List<UserGroup> userGroupList);

  @ValueMapping(source = "ADMIN", target = "ADMIN")
  @ValueMapping(source = "USER", target = "USER")
  @ValueMapping(source = "MANAGER", target = "MANAGER")
  EStateUserGroup map(EStateUserGroupCollection value);

  @ValueMapping(source = "ADMIN", target = "ADMIN")
  @ValueMapping(source = "USER", target = "USER")
  @ValueMapping(source = "MANAGER", target = "MANAGER")
  EStateUserGroupCollection map(EStateUserGroup value);

  @Named("mapUserGroupInfoList")
  default List<UserGroupInfo> mapUserGroupInfoList(List<UserGroupInfo> userGroupInfoDomainList) {
    return userGroupInfoDomainList.stream()
            .map(d -> new UserGroupInfo(
                    d.getGroupId(),
                    mapEState(d.getState()) // Assuming UserCollectionDomain has a getState method and the state conversion is needed
            ))
            .collect(Collectors.toList());
  }
  @Named("mapEState")
  default EStateUserGroupCollection mapEState(EStateUserGroupCollection state) {
    // Conversion logic for EStateUserGroupDomain to EStateUserGroupCollection
    return state != null ? EStateUserGroupCollection.valueOf(state.toString()) : null;
  }

}