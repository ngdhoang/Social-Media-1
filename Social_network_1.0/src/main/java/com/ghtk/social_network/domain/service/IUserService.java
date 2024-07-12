package com.ghtk.social_network.domain.service;

import com.ghtk.social_network.domain.model.form.UserUpdateForm;
import org.apache.catalina.User;

import java.util.Optional;

public interface IUserService {
    void update(Long id, UserUpdateForm form);

//    Optional<User> findUserById
}
