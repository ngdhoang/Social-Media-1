package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.entity.FriendShip;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetRequestFriendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendShipService implements FriendShipPort {

    private final AuthPort authenticationRepositoryPort;

    private final FriendShipPort friendShipPort;

    private final ProfilePort profilePort;

    private User getUserAuth() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        return authenticationRepositoryPort.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
    }

    @Override
    public void setRequestFriendShip(Long userReceiveId) {

    }
}
