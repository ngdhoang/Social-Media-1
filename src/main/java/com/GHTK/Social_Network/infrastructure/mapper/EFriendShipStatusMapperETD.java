package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.EFriendshipStatusEntity;
import org.springframework.stereotype.Component;

@Component
public class  EFriendShipStatusMapperETD {

  public EFriendshipStatus toDomain(EFriendshipStatusEntity eFriendshipStatusEntity) {
    if (eFriendshipStatusEntity == null) {
      return null;
    }

    return switch (eFriendshipStatusEntity) {
      case PENDING -> EFriendshipStatus.PENDING;
      case CLOSE_FRIEND -> EFriendshipStatus.CLOSE_FRIEND;
      case SIBLING -> EFriendshipStatus.SIBLING;
      case PARENT -> EFriendshipStatus.PARENT;
      case BLOCK -> EFriendshipStatus.BLOCK;
      case OTHER -> EFriendshipStatus.OTHER;
      default -> throw new IllegalArgumentException("Unknown EFriendshipStatusEntity: " + eFriendshipStatusEntity);
    };
  }

  public EFriendshipStatusEntity toEntity(EFriendshipStatus eFriendshipStatus) {
    if (eFriendshipStatus == null) {
      return null;
    }

    return switch (eFriendshipStatus) {
      case PENDING -> EFriendshipStatusEntity.PENDING;
      case CLOSE_FRIEND -> EFriendshipStatusEntity.CLOSE_FRIEND;
      case SIBLING -> EFriendshipStatusEntity.SIBLING;
      case PARENT -> EFriendshipStatusEntity.PARENT;
      case BLOCK -> EFriendshipStatusEntity.BLOCK;
      case OTHER -> EFriendshipStatusEntity.OTHER;
      default -> throw new IllegalArgumentException("Unknown EFriendshipStatus: " + eFriendshipStatus);
    };
  }
}
