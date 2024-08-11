package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;

public interface GroupPortInput {
  GroupResponse createGroup(CreateGroupRequest createGroupRequest);
}
