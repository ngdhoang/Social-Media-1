package com.GHTK.Social_Network.infrastructure.adapter.output.listener;

import com.GHTK.Social_Network.domain.event.user.CreateUserEvent;
import com.GHTK.Social_Network.domain.event.user.RemoveUserEvent;
import com.GHTK.Social_Network.domain.event.user.UpdateProfileEvent;
import com.GHTK.Social_Network.domain.model.user.Profile;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.HometownNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserEventListeners {

    private final UserNodeRepository userNodeRepository;
    private final UserCollectionRepository userCollectionRepository;

    @EventListener
    public void handleUserCreateEvent(CreateUserEvent event) {
        User user = event.getUser();
        UserNode newUserNode = new UserNode(user.getUserId(), user.getFirstName(), user.getLastName(), user.getUserEmail());
        userNodeRepository.save(newUserNode);

        UserCollection userCollection = new UserCollection(user.getUserId());
        userCollectionRepository.save(userCollection);
    }

    @EventListener
    public void handleRemoveUserEvent(RemoveUserEvent event) {
        String email = event.getEmail();
        UserNode user = userNodeRepository.getUserNodeByEmail(email);
        userNodeRepository.delete(user);
    }

    @EventListener
    public void handleUserUpdateEvent(UpdateProfileEvent event) {
        Profile profile = event.getProfile();
        Integer homeTown = profile.getHomeTown();
        Long userId = profile.getUserId();

        HometownNode hometownNode = userNodeRepository.getUserWithHometown(userId);
        if (hometownNode != null){
          if (homeTown != null && !Objects.equals(hometownNode.getHometownId(), homeTown)) {
            userNodeRepository.removeUserHometown(userId);
            userNodeRepository.setUserHometown(userId, homeTown);
          }
          else if (homeTown == null){
            userNodeRepository.removeUserHometown(userId);
          }
        } else if (hometownNode == null && homeTown != null) {
          userNodeRepository.setUserHometown(userId, homeTown);
        }
    }
}
