package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.chat.WebsocketPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.RequiredArgsConstructor;
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
  private final WebsocketClientPort webSocketClientPort;
  private final GroupPort groupPort;
  private final ChatMapper chatMapper;

  @Override
  public void handleIncomingMessage(MessageDto message) {
    UserBasicDto currentUser = webSocketClientPort.getUserAuth();
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
      return Optional.ofNullable(groupPort.getGroup(message.getGroupId()));
    } else {
      String groupId = generatePersonalGroupId(message.getGroupId());
      Group group = groupPort.getGroup(groupId);
      if (group == null) {
        List<Long> userIds = parseGroupId(message.getGroupId());
        groupPort.createGroupPersonal(userIds.get(0), userIds.get(1));
        group = groupPort.getGroup(groupId);
      }
      return Optional.ofNullable(group);
    }
  }

  // if group exist -> send to user
  // else -> send error to user
  private void handleGroupMessage(MessageDto message, UserBasicDto currentUser, Optional<Group> currentGroup) {
    currentGroup.ifPresentOrElse(
            group -> {
              if (groupPort.isUserInGroup(currentUser.getUserId(), group.getGroupId())) {
                sendToGroup(message, currentUser.getUserId(), group.getGroupId());
              } else {
                sendError(ERROR_GROUP_NOT_EXISTS, currentUser.getUserId());
              }
            },
            () -> sendError(ERROR_GROUP_NOT_EXISTS, currentUser.getUserId())
    );
  }

  // if group personal exist -> send to user
  // else -> create group -> send to user
  private void handlePersonalMessage(MessageDto message, UserBasicDto currentUser, Optional<Group> currentGroup) {
    currentGroup.ifPresentOrElse(
            group -> handleExistingPersonalGroup(message, currentUser, group),
            () -> handleNewPersonalGroup(message, currentUser)
    );
  }

  private void handleExistingPersonalGroup(MessageDto message, UserBasicDto currentUser, Group group) {
    Long userReceiveId = getUserReceiveId(
            currentUser.getUserId(),
            group.getMembers().stream().map(Member::getUserId).toList()
    );

    if (userReceiveId == null) {
      sendError(ERROR_USER_NOT_EXISTS, currentUser.getUserId());
    } else if (validateFriendship(currentUser.getUserId(), userReceiveId)) {
      sendToUser(message, currentUser.getUserId(), userReceiveId);
    }
  }

  private void handleNewPersonalGroup(MessageDto message, UserBasicDto currentUser) {
    Long userReceiveId = getUserReceiveId(
            currentUser.getUserId(),
            parseGroupId(message.getGroupId())
    );

    if (userReceiveId != null && validateFriendship(currentUser.getUserId(), userReceiveId)) {
      groupPort.createGroupPersonal(currentUser.getUserId(), userReceiveId);
      sendToUser(message, currentUser.getUserId(), userReceiveId);
    }
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

  private Long getUserReceiveId(Long currentUserId, List<Long> memberIds) {
    return memberIds.stream()
            .filter(id -> !id.equals(currentUserId))
            .findFirst()
            .orElse(null);
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
    Message message = chatMapper.messageDtoToMessage(msg);
    message.setUserAuthId(userIdSend);
    webSocketClientPort.sendUserAndSave(EGroupType.PERSONAL, message, "/channel/app" + userReceiveId);
  }

  private void sendToGroup(MessageDto msg, Long userIdSend, String groupId) {
    Message message = chatMapper.messageDtoToMessage(msg);
    message.setUserAuthId(userIdSend);
    webSocketClientPort.sendListUserAndSave(message, groupPort.getGroup(groupId).getMembers().stream().map(Member::getUserId).toList());
  }

  private void sendError(String error, Long userReceiveId) {
    webSocketClientPort.sendUserError(error, userReceiveId);
  }
}