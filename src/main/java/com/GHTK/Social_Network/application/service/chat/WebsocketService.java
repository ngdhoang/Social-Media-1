package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.WebsocketPortInput;
import com.GHTK.Social_Network.application.port.output.ChatPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.WebsocketPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.adapter.input.websocket.WebsocketContextHolder;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebsocketService implements WebsocketPortInput {
  private static final String GROUP_ID_SEPARATOR = "_";
  private static final String ERROR_GROUP_NOT_EXISTS = "Group does not exist";
  private static final String ERROR_USER_NOT_EXISTS = "User does not exist";
  private static final String ERROR_USER_BLOCKED = "User is blocked";
  private static final String ERROR_USER_NOT_FRIEND = "User is not a friend";

  private final FriendShipPort friendShipPort;
  private final WebsocketPort webSocketPort;
  private final ChatPort chatPort;
  private final ChatMapper chatMapper;

  @Override
  public void handleIncomingMessage(MessageDto message) {
    UserBasicDto currentUser = webSocketPort.getUserAuth();
    boolean isGroup = message.getGroupType().equals(EGroupType.GROUP);

    Optional<Group> currentGroup = getOrCreateGroup(message, isGroup);

    if (isGroup) {
      handleGroupMessage(message, currentUser, currentGroup);
    } else {
      handlePersonalMessage(message, currentUser, currentGroup);
    }
  }

  private Optional<Group> getOrCreateGroup(MessageDto message, boolean isGroup) {
    if (isGroup) {
      return Optional.ofNullable(chatPort.getGroup(message.getGroupId()));
    } else {
      String groupId = generatePersonalGroupId(message.getGroupId());
      Group group = chatPort.getGroup(groupId);
      if (group == null) {
        List<Long> userIds = parseGroupId(message.getGroupId());
        chatPort.createGroupPersonal(userIds.get(0), userIds.get(1));
        group = chatPort.getGroup(groupId);
      }
      return Optional.ofNullable(group);
    }
  }

  private void handleGroupMessage(MessageDto message, UserBasicDto currentUser, Optional<Group> currentGroup) {
    currentGroup.ifPresentOrElse(
            group -> {
              if (chatPort.isUserInGroup(currentUser.getUserId(), group.getGroupId())) {
                sendToGroup(message, currentUser.getUserId(), group.getGroupId());
              } else {
                sendError(ERROR_GROUP_NOT_EXISTS, currentUser.getUserId());
              }
            },
            () -> sendError(ERROR_GROUP_NOT_EXISTS, currentUser.getUserId())
    );
  }

  private void handlePersonalMessage(MessageDto message, UserBasicDto currentUser, Optional<Group> currentGroup) {
    currentGroup.ifPresentOrElse(
            group -> {
              Long userReceiveId = getUserReceiveId(currentUser.getUserId(), group.getMembers());
              if (validateFriendship(currentUser.getUserId(), userReceiveId)) {
                sendToUser(message, currentUser.getUserId(), userReceiveId);
              }
            },
            () -> sendError(ERROR_GROUP_NOT_EXISTS, currentUser.getUserId())
    );
  }

  private boolean validateFriendship(Long currentId, Long userId) {
    if (friendShipPort.isDeleteUser(userId)) {
      sendError(ERROR_USER_NOT_EXISTS, currentId);
      return false;
    }

    if (friendShipPort.isBlock(currentId, userId)) {
      sendError(ERROR_USER_BLOCKED, currentId);
      return false;
    }

    if (!friendShipPort.isFriend(currentId, userId)) {
      sendError(ERROR_USER_NOT_FRIEND, currentId);
      return false;
    }

    return true;
  }

  private Long getUserReceiveId(Long currentUserId, List<Member> members) {
    return members.stream()
            .map(Member::getUserId)
            .filter(id -> !id.equals(currentUserId))
            .findFirst()
            .orElseThrow(() -> new CustomException("No receiver found in the group", HttpStatus.NOT_FOUND));
  }

  private List<Long> parseGroupId(String groupId) {
    return Arrays.stream(groupId.split(GROUP_ID_SEPARATOR))
            .map(Long::parseLong)
            .sorted()
            .toList();
  }

  private String generatePersonalGroupId(String groupId) {
    List<Long> sortedIds = parseGroupId(groupId);
    return String.format("%d%s%d", sortedIds.get(0), GROUP_ID_SEPARATOR, sortedIds.get(1));
  }

  private void sendToUser(MessageDto msg, Long userIdSend, Long userReceiveId) {
    webSocketPort.sendAndSave(msg.getGroupType(), chatMapper.messageDtoToMessage(msg), userIdSend, userReceiveId);
  }

  private void sendToGroup(MessageDto msg, Long userIdSend, String groupId) {
    chatPort.getGroup(groupId).getMembers().forEach(member ->
            sendToUser(msg, userIdSend, member.getUserId())
    );
  }

  private void sendError(String error, Long userReceiveId) {
    webSocketPort.sendErrorForMe(error, userReceiveId);
  }
}