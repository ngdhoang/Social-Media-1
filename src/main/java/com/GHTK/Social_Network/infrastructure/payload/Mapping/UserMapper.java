package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "userId", target = "userId")
    User UserDtoToUser(UserDto profile);

    @Mapping(source = "userId", target = "userId")
    UserDto userToUserDto(User user);

    @Mapping(source = "userId", target = "userId")
    UserBasicDto userToUserBasicDto(User user);

    @Mapping(source = "userId", target = "userId")
    User UserBasicDtoToUser(UserBasicDto profile);
}
