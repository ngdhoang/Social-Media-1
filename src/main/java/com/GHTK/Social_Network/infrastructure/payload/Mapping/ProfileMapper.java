package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(source = "profileId", target = "userId")
    User profileToUser(ProfileDto profile);

    @Mapping(source = "userId", target = "profileId")
    ProfileDto userToProfileDto(User user);
}
