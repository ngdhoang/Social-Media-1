package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;

import java.util.List;

public interface SearchPort {
  List<UserEntity> searchUserInPage(String keyword);
}
