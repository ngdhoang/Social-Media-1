package com.GHTK.Social_Network.domain.event.user;

import lombok.Data;

@Data
public class RemoveUserEvent {
    private String email;
}
