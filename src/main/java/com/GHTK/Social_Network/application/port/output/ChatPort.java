package com.GHTK.Social_Network.application.port.output;

import java.util.List;

public interface ChatPort {
  List<Long> getUserIdsFromChannel(Long channelId);
}
