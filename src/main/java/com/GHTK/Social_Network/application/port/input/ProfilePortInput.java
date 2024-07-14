package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ProfileResponse;

public interface ProfilePortInput {
    ProfileResponse takeProfile(ProfileRequest profileRequest);
    Boolean updateProfile(ProfileRequest profileRequest);
}
