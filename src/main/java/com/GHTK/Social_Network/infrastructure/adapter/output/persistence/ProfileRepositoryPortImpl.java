package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProfileRepositoryPortImpl implements ProfilePort {
    private final UserRepository userRepo;

    @Override
    public boolean isSelfProfileClicked(Long idUser, Long idProfile) {
        if(idUser.equals(idProfile)){
            return true;
        }
        return false;
    }

    @Override
    public User findUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public void saveUser(User user) {
        userRepo.save(user);
    }
}
