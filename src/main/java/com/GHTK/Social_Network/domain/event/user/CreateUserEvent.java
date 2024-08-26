package com.GHTK.Social_Network.domain.event.user;

import com.GHTK.Social_Network.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserEvent {
    private User user;


}