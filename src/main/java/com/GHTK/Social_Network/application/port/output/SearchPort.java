package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;

import java.util.List;

public interface SearchPort {
  List<User> searchUserInPage(String keyword);
}
