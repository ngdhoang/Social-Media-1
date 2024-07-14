package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfilePortInput {
    private final ProfilePort profilePort;


    @Override
    public ProfileResponse takeProfile(ProfileRequest profileRequest) {
        //idUser view profile
        Long idViewUser = profileRequest.getIdUser();
        // idUser contains profile
        Long idProfileUser = profileRequest.getIdProfile();
        User user = profilePort.findUserById(profileRequest.getIdProfile());
        ProfileResponse profileResponse = new ProfileResponse();
        BeanUtils.copyProperties(user, profileResponse);
        if(profilePort.isSelfProfileClicked(idViewUser,idProfileUser)) {
            profileResponse.setMessage("Profile User");
            return profileResponse;
        }
        profileResponse.setMessage("Profile User Other");
        return profileResponse;


    }

    @Override
    public Boolean updateProfile(ProfileRequest profileRequest) {
        //iduser view profile
        Long idViewUser = profileRequest.getIdUser();
        //idUser contains profile
        Long idProfileUser = profileRequest.getIdProfile();
        // chi User chinh chu moi duoc edit profile
        // true view chinh chu
        // false user view profile user other
        if(profilePort.isSelfProfileClicked(idViewUser,idProfileUser)) {
            User user = profilePort.findUserById(profileRequest.getIdProfile());
            BeanUtils.copyProperties(profileRequest, user);
            profilePort.saveUser(user);
            return true;
        }
        return false;
    }
}
