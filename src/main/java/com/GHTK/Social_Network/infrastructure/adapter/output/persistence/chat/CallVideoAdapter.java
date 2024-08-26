package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.CallVideoPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.application.port.output.chat.redis.RedisWebsocketPort;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CallVideoAdapter implements CallVideoPort {
  private final WebsocketClientPort websocketClientPort;
  private final GroupPort groupPort;

  private final RedisWebsocketPort redisWebsocketPort;

  @Override
  public void handlerCallVideo(Long userId) {
    Set<Group> groupList = groupPort.getGroupsByUserId(userId);

    groupList.stream().forEach(g -> {
      String groupId = g.getGroupType().equals(EGroupType.PERSONAL) ? g.getGroupName() : g.getId();
      List<String> keys = redisWebsocketPort.getKeysByTail(RedisWebsocketPort.RING + groupId);
      if (!keys.isEmpty()) {
        String key = keys.get(0);
        if (key != null) {
          Message message = Message.builder()
                  .userAuthId(Long.valueOf(key.split(RedisWebsocketPort.RING)[0]))
                  .groupId(key.split(RedisWebsocketPort.RING)[1])
                  .msgType(EMessageType.CALL)
                  .content("Ring ring ring...")
                  .build();
          websocketClientPort.sendUserAndNotSave(message, "/channel/app/" + userId);
        }
      }
    });
  }

}
