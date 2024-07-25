package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.EReactionType;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionPostUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ReactionPostInfoMapper {

  @Mapping(source = "user.userId", target = "profileId")
  @Mapping(source = "user", target = ".")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionPostUserDto toReactionPostInfoResponse(User user, EReactionType reactionType);

  @Named("toUserResponse")
  default ReactionPostUserDto toUserResponse(User user) {
    return ReactionPostUserDto.builder()
            .profileId(user.getUserId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .avatar(user.getAvatar())
            .userEmail(user.getUserEmail())
            .dob(user.getDob())
            .phoneNumber(user.getPhoneNumber())
            .homeTown(user.getHomeTown())
            .schoolName(user.getSchoolName())
            .workPlace(user.getWorkPlace())
            .isProfilePublic(user.getIsProfilePublic())
            .build();
  }
}