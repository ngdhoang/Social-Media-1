package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.application.port.input.chat.WebsocketPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.MessagePort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
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

  private final GroupPortInput groupPortInput;

  private final FriendShipPort friendShipPort;
  private final WebsocketClientPort webSocketClientPort;
  private final GroupPort groupPort;
  private final MessagePort messagePort;

  private final ChatMapper chatMapper;

  @Override
  public void handleIncomingMessage(MessageDto message) {
    UserBasicDto currentUser = webSocketClientPort.getUserAuth();

    boolean isGroup = message.getGroupType().equals(EGroupType.GROUP);

    if (isGroup) {
      Optional<Group> currentGroup = Optional.ofNullable(groupPort.getGroupForGroup(message.getGroupId()));
      handleGroupMessage(message, currentUser, currentGroup);
    } else {
      Optional<Group> currentGroup = Optional.ofNullable(groupPort.getGroupForPersonal(message.getGroupId()));
      handlePersonalMessage(message, currentUser, currentGroup);
    }
  }

  // if group exist -> send to user
  // else -> send error to user
  private void handleGroupMessage(MessageDto message, UserBasicDto currentUser, Optional<Group> currentGroup) {
    currentGroup.ifPresentOrElse(
            group -> {
              boolean checkTag = validateTagUsers(currentUser.getUserId(), message.getTags(), group.getMembers().stream().map(Member::getUserId).toList());
              if (groupPort.isUserInGroup(currentUser.getUserId(), group.getId()) && checkTag) {
                if (message.getReplyMsgId() != null) {
                  Message messageQuote = messagePort.getMessageById(message.getReplyMsgId());
                  if (validateReplyUser(currentUser.getUserId(), messageQuote.getUserAuthId())) {
                    sendReplyToGroup(message, messageQuote, currentUser.getUserId(), group.getId());
                  }
                } else {
                  sendToGroup(message, currentUser.getUserId(), group.getId());
                }
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

    boolean checkTag = validateTagUsers(currentUser.getUserId(), message.getTags(), Collections.singletonList(userReceiveId));
    if (userReceiveId == null || !checkTag) {
      sendError(ERROR_USER_NOT_EXISTS, currentUser.getUserId());
    } else if (validateFriendship(currentUser.getUserId(), userReceiveId)) {
      if (message.getReplyMsgId() != null) {
        Message messageQuote = messagePort.getMessageById(message.getReplyMsgId());
        if (validateReplyUser(currentUser.getUserId(), messageQuote.getUserAuthId())) {
          sendReplyToGroup(message, messageQuote, currentUser.getUserId(), group.getId());
        }
      } else {
        sendToUser(message, currentUser.getUserId(), userReceiveId);
      }
    }
  }

  private void handleNewPersonalGroup(MessageDto message, UserBasicDto currentUser) {
    Long userReceiveId = getUserReceiveId(
            currentUser.getUserId(),
            parseGroupId(message.getGroupId())
    );

    boolean checkTag = validateTagUsers(currentUser.getUserId(), message.getTags(), Collections.singletonList(userReceiveId));
    if (userReceiveId != null && validateFriendship(currentUser.getUserId(), userReceiveId) && checkTag) {
      groupPort.createGroupPersonal(currentUser.getUserId(), userReceiveId);

      sendToUser(message, currentUser.getUserId(), userReceiveId);
    } else {
      sendError(ERROR_USER_NOT_EXISTS, currentUser.getUserId());
    }
  }

  private boolean validateReplyUser(Long currentUserId, Long replyUserId) {
    if (friendShipPort.isDeleteUser(replyUserId) && replyUserId != null) {
      sendError(ERROR_USER_BLOCKED, currentUserId);
      return false;
    }
    return true;
  }

  private boolean validateTagUsers(Long currentUserId, List<Long> tagUserIds, List<Long> memberList) {
    for (Long id : tagUserIds) {
      if (friendShipPort.isDeleteUser(id)) {
        sendError(ERROR_USER_BLOCKED, currentUserId);
        return false;
      }

      if (memberList.contains(id)) {
        sendError(ERROR_USER_BLOCKED, currentUserId);
        return false;
      }
    }
    return true;
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

  private void sendToUser(MessageDto msg, Long userIdSend, Long userReceiveId) {
    Message message = chatMapper.messageDtoToMessage(msg);
    message.setUserAuthId(userIdSend);
    webSocketClientPort.sendUserAndSave(EGroupType.PERSONAL, message, "/channel/app/" + userReceiveId);
  }

  private void sendReplyToUser(MessageDto msgReply, Message msg, Long userIdSend, Long userReceiveId) {
    Message messageReply = chatMapper.messageDtoToMessage(msgReply);
    messageReply.setUserAuthId(userIdSend);
    webSocketClientPort.sendRelyUserAndSave(EGroupType.PERSONAL, messageReply, msg, "/channel/app/" + userReceiveId);
  }

  private void sendToGroup(MessageDto msg, Long userIdSend, String groupId) {
    Message message = chatMapper.messageDtoToMessage(msg);
    message.setUserAuthId(userIdSend);
    webSocketClientPort.sendListUserAndSave(message, groupPort.getGroupForPersonal(groupId).getMembers().stream().map(Member::getUserId).toList());
  }

  private void sendReplyToGroup(MessageDto msgReply, Message msg, Long userIdSend, String groupId) {
    Message messageReply = chatMapper.messageDtoToMessage(msgReply);
    messageReply.setUserAuthId(userIdSend);
    webSocketClientPort.sendReplyListUserAndSave(messageReply, msg, groupPort.getGroupForPersonal(groupId).getMembers().stream().map(Member::getUserId).toList());
  }

  private void sendError(String error, Long userReceiveId) {
    webSocketClientPort.sendUserError(error, userReceiveId);
  }
}