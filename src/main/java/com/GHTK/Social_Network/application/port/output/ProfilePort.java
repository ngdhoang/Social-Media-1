package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.user.User;
import org.springframework.context.annotation.Profile;

public interface ProfilePort {
    boolean isSelfProfileClicked(Long idUser,Long idProfile);
    User findUserById(Long id);
    void saveUser(User user);
}
