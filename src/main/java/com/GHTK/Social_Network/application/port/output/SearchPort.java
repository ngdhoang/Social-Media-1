package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.user.User;

import java.util.List;

public interface SearchPort {
  List<User> searchUserInPage(String keyword);
}
