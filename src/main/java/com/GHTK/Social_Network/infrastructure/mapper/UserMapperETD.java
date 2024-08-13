package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.user.Profile;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.domain.model.user.EGender;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.ProfileEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.EGenderEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface UserMapperETD {

  UserEntity toEntity(User user);

  User toDomain(UserEntity userEntity);

  @Mapping(source = "gender", target = "gender", qualifiedByName = "mapGenderToDomain")
  Profile toDomain(ProfileEntity profileEntity);

  @Mapping(source = "gender", target = "gender", qualifiedByName = "mapGenderToEntity")
  ProfileEntity toEntity(Profile profile);

  @Mapping(source = "userId", target = "userId")
  UserNode userNodeToEntity(UserEntity user);

  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "userEmail", target = "email")
  UserNode userDomainToNode(User user);

  @Mapping(source = "userId", target = "userId")
  User userNodeToDomain(UserNode userNode);

  @Named("mapGenderToDomain")
  @ValueMappings({
          @ValueMapping(source = "MALE", target = "MALE"),
          @ValueMapping(source = "FEMALE", target = "FEMALE"),
          @ValueMapping(source = "OTHER", target = "OTHER")
  })
  EGender mapGenderToDomain(EGenderEntity gender);

  @Named("mapGenderToEntity")
  @ValueMappings({
          @ValueMapping(source = "MALE", target = "MALE"),
          @ValueMapping(source = "FEMALE", target = "FEMALE"),
          @ValueMapping(source = "OTHER", target = "OTHER")
  })
  EGenderEntity mapGenderToEntity(EGender gender);
}
