package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.chat.MessagePortInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.MessagePort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MessageService implements MessagePortInput {
  private final ImageHandlerPortInput imageHandlerPortInput;
  private final CloudPort cloudPort;

  private final AuthPort authPort;
  private final GroupPort groupPort;
  private final MessagePort messagePort;
  private final WebsocketClientPort websocketClientPort;

  private final ChatMapper chatMapper;

  @Override
  public ChatMessageResponse deleteMessage(String messageId) {
    return processMessageOperation(messageId, EMessageType.DELETE);
  }

  @Override
  public ChatMessageResponse recallMessage(String messageId) {
    return processMessageOperation(messageId, EMessageType.RECALL);
  }

  @Override
  public ChatMessageResponse reactionMessage(String messageId) {
    User currentUser = authPort.getUserAuth();
    Message message = getValidatedMessage(messageId);
    Group group = validateOperationMessage(currentUser, message, false);

    List<Long> reactionList = message.getReaction() == null ? new ArrayList<>() : message.getReaction();
    reactionList.add(currentUser.getUserId());
    message.setReaction(reactionList);

    websocketClientPort.sendListUserAndSave(message, group.getMembers().stream().map(Member::getUserId).toList());
    return chatMapper.messageToMessageResponse(message, group.getGroupType());
  }

  @Override
  public MessageResponse sendListImage(List<MultipartFile> images, String groupId) {
    User currentUser = authPort.getUserAuth();
    Group group = getGroup(groupId);

    List<CompletableFuture<Optional<String>>> futures = images.stream()
            .map(image -> CompletableFuture.supplyAsync(() -> {
              if (!imageHandlerPortInput.checkSizeValid(image, ImageHandlerPortInput.MAX_SIZE_NOT_VALID) ||
                      !imageHandlerPortInput.isImage(image)) {
                return Optional.<String>empty();
              }
              try {
                String imageUrl = cloudPort.extractByKey(cloudPort.uploadPictureByFile(image), "url");
                return Optional.of(imageUrl);
              } catch (Exception e) {
                return Optional.<String>empty();
              }
            }))
            .toList();

    List<String> successfulUploads = futures.stream()
            .map(CompletableFuture::join)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();


    Message message = Message.builder()
            .userAuthId(currentUser.getUserId())
            .images(successfulUploads)
            .msgType(EMessageType.IMAGE)
            .build();

    websocketClientPort.sendListUserAndSave(message, getGroupMemberIds(group.getMembers()));

    return new MessageResponse("Processing completed. Successfully uploaded " + successfulUploads.size() + " images.");
  }

  private ChatMessageResponse processMessageOperation(String messageId, EMessageType operationType) {
    User currentUser = authPort.getUserAuth();
    Message message = getValidatedMessage(messageId);
    Group group = validateOperationMessage(currentUser, message, true);

    EMessageType newMessageType = determineNewMessageType(message, operationType);
    updateMessage(message, newMessageType);

    notifyGroupMembers(message, group);

    return chatMapper.messageToMessageResponse(message, group.getGroupType());
  }

  private Message getValidatedMessage(String messageId) {
    Message message = messagePort.getMessageById(messageId);
    if (message == null) {
      throw new CustomException("Message not found", HttpStatus.NOT_FOUND);
    }
    return message;
  }

  private EMessageType determineNewMessageType(Message message, EMessageType operationType) {
    if (message.getMsgType() == EMessageType.DELETE && operationType == EMessageType.RECALL) {
      throw new CustomException("Cannot recall a deleted message", HttpStatus.BAD_REQUEST);
    }
    if (operationType == EMessageType.DELETE && message.getMsgType() == EMessageType.RECALL) {
      return EMessageType.DELETE_RECALL;
    }
    return operationType;
  }

  private void updateMessage(Message message, EMessageType newMessageType) {
    message.setMsgType(newMessageType);
    if (newMessageType == EMessageType.RECALL) {
      message.setContent(null);
    }
    messagePort.saveMessage(message);
  }

  private void notifyGroupMembers(Message message, Group group) {
    List<Long> memberIds = group.getMembers().stream()
            .map(Member::getUserId)
            .toList();
    websocketClientPort.sendListUserAndNotSave(message, memberIds);
  }

  private Group validateOperationMessage(User currentUser, Message message, boolean mine) {
    Group group = getGroup(message.getGroupId());
    boolean isMessageAuthor = message.getUserAuthId().equals(currentUser.getUserId());
    boolean isGroupMember = group.getMembers().stream()
            .map(Member::getUserId)
            .anyMatch(id -> id.equals(currentUser.getUserId()));

    if (!(mine == isMessageAuthor) || !isGroupMember ) {
      throw new CustomException("You are not allowed to modify this message", HttpStatus.FORBIDDEN);
    }

    return group;
  }

  private List<Long> getGroupMemberIds(List<Member> members) {
    return members.stream().map(Member::getUserId).toList();
  }

  private Group getGroup(String groupId) {
    boolean isGroupPersonal = groupId.contains("_");
    return isGroupPersonal ? groupPort.getGroupForPersonal(groupId) : groupPort.getGroupForGroup(groupId);
  }
}