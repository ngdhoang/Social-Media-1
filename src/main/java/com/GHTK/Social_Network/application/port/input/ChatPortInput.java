package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.GroupResponse;

public interface ChatPortInput {
  GroupResponse createGroup(CreateGroupRequest createGroupRequest);
}
