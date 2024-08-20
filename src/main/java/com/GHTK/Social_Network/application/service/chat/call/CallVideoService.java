package com.GHTK.Social_Network.application.service.chat.call;

import com.GHTK.Social_Network.application.port.input.chat.CallVideoPortInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.application.port.output.chat.redis.RedisWebsocketPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CallVideoService implements CallVideoPortInput {
  private final WebsocketClientPort websocketClientPort;
  private final GroupPort groupPort;
  private final AuthPort authPort;

  private final RedisWebsocketPort redisWebsocketPort;

  @Override
  public MessageResponse ring(String groupId) {
    User currentUser = authPort.getUserAuth();
    Group group = getGroup(groupId);

    validateGroup(group, currentUser.getUserId());

    Message message = Message.builder()
            .userAuthId(currentUser.getUserId())
            .groupId(groupId)
            .msgType(EMessageType.CALL)
            .content("Ring ring ring...")
            .build();
    websocketClientPort.sendListUserAndNotSave(message, getGroupMemberIds(group.getMembers()));

    redisWebsocketPort.createOrUpdateCallVideo(currentUser.getUserId() + RedisWebsocketPort.RING + groupId, null);

    return new MessageResponse("Connecting...");
  }

  private void validateGroup(Group group, Long userId) {
    if (group == null) {
      throw new CustomException("Group is null", HttpStatus.BAD_REQUEST);
    }

    if (!groupPort.isUserInGroup(userId, group.getId())) {
      throw new CustomException("One or more members do not have a valid id", HttpStatus.BAD_REQUEST);
    }
  }

  private Group getGroup(String groupId) {
    boolean isGroupPersonal = groupId.contains("_");
    return isGroupPersonal ? groupPort.getGroupForPersonal(groupId) : groupPort.getGroupForGroup(groupId);
  }

  private List<Long> getGroupMemberIds(List<Member> members) {
    return members.stream().map(Member::getUserId).toList();
  }

}
