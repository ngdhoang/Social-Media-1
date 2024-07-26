package com.GHTK.Social_Network.infrastructure.payload.dto.redis;

import com.GHTK.Social_Network.domain.model.Profile;
import com.GHTK.Social_Network.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRedisDto {
  private User user;

  private Profile profile;
}
