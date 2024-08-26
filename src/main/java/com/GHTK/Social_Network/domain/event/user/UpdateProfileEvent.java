package com.GHTK.Social_Network.domain.event.user;

import com.GHTK.Social_Network.domain.model.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class UpdateProfileEvent {
    private Profile profile;
}
