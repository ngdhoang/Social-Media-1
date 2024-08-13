package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;

import java.util.List;

public interface SearchPortInput {
  List<UserBasicDto> searchPublic(String keyword, Integer scope);
}
