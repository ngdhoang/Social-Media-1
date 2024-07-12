package com.ghtk.social_network.domain.service;

import com.ghtk.social_network.domain.model.form.UserUpdateForm;
import com.ghtk.social_network.domain.model.user.Users;
import com.ghtk.social_network.infrastructure.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService implements IUserService {

    private UserRepository repository;
    private  ModelMapper mapper;
    private  PasswordEncoder encoder;
    @Override
    public void update(Long id, UserUpdateForm form) {
        Users users = mapper.map(form, Users.class);
        String encodedPassword = encoder.encode(users.getPassword());
        users.setPassword(encodedPassword);
        users.setUserId(id);
        repository.save(users);
    }

}
