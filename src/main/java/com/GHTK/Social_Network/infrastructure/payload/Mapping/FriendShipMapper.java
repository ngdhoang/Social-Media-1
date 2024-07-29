package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.FriendShipResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FriendShipMapper {
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "userEmail", source = "userEmail")
    @Mapping(target = "avatar", source = "avatar")
    UserBasicDto userToUserDto(User user);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "friendshipStatus", target = "status")
    @Mapping(source = "mutualFriendsQuantity", target = "mutualFriendsQuantity")
    FriendShipUserDto toFriendShipUserDto(User user, EFriendshipStatus friendshipStatus, Long mutualFriendsQuantity);

    @Named("mapUserWithFriends")
    default FriendShipUserDto userToFriendShipUserDtoWithFriends(User user, List<User> friends, EFriendshipStatus status, Long mutualFriendsQuantity) {
        FriendShipUserDto dto = new FriendShipUserDto();
        dto.setUser(userToUserDto(user));
        dto.setStatus(status);
        dto.setMutualFriendsQuantity(mutualFriendsQuantity);
        return dto;
    }

    default FriendShipResponse toFriendShipResponseWithCount(List<FriendShipUserDto> friendshipUsers) {
        FriendShipResponse response = new FriendShipResponse();
        response.setFriendshipUsers(friendshipUsers);
        response.setFriendQuantity((long) friendshipUsers.size());
        return response;
    }

    default FriendShipResponse toFriendShipResponseWithoutCount(List<FriendShipUserDto> friendshipUsers) {
        FriendShipResponse response = new FriendShipResponse();
        response.setFriendshipUsers(friendshipUsers);
        return response;
    }

    default List<FriendShipUserDto> usersToFriendShipUserDtos(List<User> users, List<List<User>> friendsLists, List<EFriendshipStatus> statuses, List<Long> mutualFriendsCounts) {
        List<FriendShipUserDto> friendshipUserDtos = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            List<User> friends = friendsLists.get(i);
            EFriendshipStatus status = statuses.get(i);
            Long mutualFriendsCount = mutualFriendsCounts.get(i);
            FriendShipUserDto dto = userToFriendShipUserDtoWithFriends(user, friends, status, mutualFriendsCount);
            friendshipUserDtos.add(dto);
        }
        return friendshipUserDtos;
    }
}