package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.chat.MessagePortInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.MessagePort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.UserGroup;
import com.GHTK.Social_Network.domain.collection.chat.*;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.ReactionChatResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.ChatMessageReplyResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.ChatMessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.ListChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
  private final UserMapper userMapper;

  @Override
  public ChatMessageResponse deleteMessage(String messageId) {
    return processMessageOperation(messageId, EMessageType.DELETE);
  }

  @Override
  public ChatMessageResponse recallMessage(String messageId) {
    return processMessageOperation(messageId, EMessageType.RECALL);
  }

  @Override
  public ChatMessageResponse reactionMessage(String messageId, ReactionRequest reactionRequest) {
    User currentUser = authPort.getUserAuth();
    Message message = getValidatedMessage(messageId);
    Group group = validateOperationMessage(currentUser, message, false);

    messagePort.saveOrChangeReactionMessage(messageId, currentUser.getUserId(), stringToReactionType(
            reactionRequest.getReactionType()
    ));

    websocketClientPort.sendListUserAndNotSave(message, group.getMembers().stream().map(Member::getUserId).toList());
    return chatMapper.messageToMessageResponse(message, userMapper.userToUserBasicDto(currentUser), group.getGroupType());
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

  @Override
  public ReactionChatResponse getReactionMessage(String messageId, String status) {
    Message message = getValidatedMessage(messageId);
    Group group = getGroup(message.getGroupId());
    if (group == null) {
      throw new CustomException("Message not found", HttpStatus.NOT_FOUND);
    }

    List<ReactionMessages> reactionMessagesList = message.getReactionMsgs();

    ReactionChatResponse reactionChatResponse = new ReactionChatResponse();
    List<UserBasicDto> userBasicDtoList = new ArrayList<>();
    reactionMessagesList.forEach(
            reactionMessage -> {
              if (reactionMessage.getReactionType().toString().toLowerCase().equals(status)) {
                UserBasicDto userBasicDto = userMapper.userToUserBasicDto(authPort.getUserById(reactionMessage.getUserId()));
                userBasicDtoList.add(userBasicDto);
              }

              switch (reactionMessage.getReactionType()) {
                case LIKE -> reactionChatResponse.setCountLike(reactionChatResponse.getCountLike() + 1);
                case ANGRY -> reactionChatResponse.setCountAngry(reactionChatResponse.getCountAngry() + 1);
                case LOVE -> reactionChatResponse.setCountLove(reactionChatResponse.getCountLove() + 1);
                default -> reactionChatResponse.setCountSmile(reactionChatResponse.getCountSmile() + 1);
              }

            }
    );

    reactionChatResponse.setUser(userBasicDtoList);
    reactionChatResponse.setType(status);
    return reactionChatResponse;
  }

  @Override
  public ListChatMessageResponse getMessages(String groupId, PaginationRequest paginationRequest) {
    List<ChatMessageReplyResponse> chatMessageReplyResponses = messagePort.getMessagesByGroupId(groupId, paginationRequest).stream().map(
            m -> {
              Message messageChild = m.getRight();
              Message messageParent = m.getLeft();

              Group group = getGroup(messageChild.getGroupId());

              ChatMessageResponse chatMessageResponseParent = null;
              if (messageParent != null) {
                chatMessageResponseParent = chatMapper.messageToMessageResponse(
                        messageParent,
                        userMapper.userToUserBasicDto(authPort.getUserById(messageParent.getUserAuthId())),
                        group.getGroupType());
              }
              ChatMessageResponse chatMessageResponse = chatMapper.messageToMessageResponse(
                      messageChild,
                      userMapper.userToUserBasicDto(authPort.getUserById(messageChild.getUserAuthId())),
                      group.getGroupType());

              return new ChatMessageReplyResponse(
                      chatMessageResponseParent,
                      chatMessageResponse
              );
            }
    ).toList();

    if (!chatMessageReplyResponses.isEmpty()) {
      String lastMsgId = chatMessageReplyResponses.get(chatMessageReplyResponses.size() - 1).getMsgReply().getMsgId();
      readMessages(lastMsgId);
    }

    Group group = getGroup(groupId);
    Message lastMessage = messagePort.getLastMessageByGroupId(groupId);
    List<UserBasicDto> userBasicDtoList = new ArrayList<>();
    group.getMembers().forEach(member -> {
      if (member.getLastTimeMsgSeen() != null) {
        if (member.getLastTimeMsgSeen().isAfter(lastMessage.getCreateAt())) {
          userBasicDtoList.add(
                  userMapper.userToUserBasicDto(authPort.getUserById(member.getUserId()))
          );
        }
      }
    });

    return new ListChatMessageResponse(
            chatMessageReplyResponses,
            userBasicDtoList,
            userBasicDtoList.size()
    );
  }

  @Override
  public MessageResponse readMessages(String msgId) {
    User currentUser = authPort.getUserAuth();
    Message message = getValidatedMessage(msgId);
    Group group = getValidatedGroup(message.getGroupId());

    Member currentMember = findCurrentMember(group, currentUser.getUserId());
    if (currentMember == null) {
      throw new CustomException("User is not a member of this group", HttpStatus.FORBIDDEN);
    }

    if (shouldUpdateLastSeen(currentMember)) {
      updateMemberLastSeen(currentMember, msgId);
      updateGroupMember(group, currentMember);
      if (group.getGroupType().isGroupPublic(group.getGroupType())) {
        updateUserCollectionLastSeen(currentUser.getUserId(), group.getId(), msgId);
      } else {
        updateUserCollectionLastSeen(currentUser.getUserId(), group.getGroupName(), msgId);
      }
      return new MessageResponse("Message read successfully");
    }

    return new MessageResponse("No update required");
  }

  @Override
  public MessageResponse typingMessages(String groupId) {
    User currentUser = authPort.getUserAuth();
    UserCollectionDomain userCollectionDomain = authPort.getUserCollectionById(currentUser.getUserId());
    Group group = getValidatedGroup(groupId);

    for (UserGroup userGroupInfo : userCollectionDomain.getUserGroupInfoList()) {
      if (userGroupInfo.getGroupId().equals(groupId)) {
        websocketClientPort.sendListUserAndNotSave(
                Message.builder()
                        .userAuthId(currentUser.getUserId())
                        .content("Typing")
                        .msgType(EMessageType.TYPING)
                        .build(),
                group.getMembers().stream().map(
                        Member::getUserId
                ).toList()
        );
        break;
      }
    }

    return new MessageResponse("Message typing successfully");
  }


  private Group getValidatedGroup(String groupId) {
    Group group = getGroup(groupId);
    if (group == null) {
      throw new CustomException("Group not found", HttpStatus.NOT_FOUND);
    }
    return group;
  }

  private Member findCurrentMember(Group group, Long userId) {
    return group.getMembers().stream()
            .filter(member -> member.getUserId().equals(userId))
            .findFirst()
            .orElse(null);
  }

  private boolean shouldUpdateLastSeen(Member member) {
    if (member.getLastTimeMsgSeen() == null) return true;
    return Instant.now().isAfter(member.getLastTimeMsgSeen());
  }

  private void updateMemberLastSeen(Member member, String msgId) {
    member.setLastTimeMsgSeen(Instant.now());
    member.setLastMsgSeen(msgId);
  }

  private void updateGroupMember(Group group, Member updatedMember) {
    List<Member> updatedMembers = group.getMembers().stream()
            .map(member -> member.getUserId().equals(updatedMember.getUserId()) ? updatedMember : member)
            .collect(Collectors.toList());
    group.setMembers(updatedMembers);
    groupPort.saveGroup(group);
  }

  private void updateUserCollectionLastSeen(Long userId, String groupId, String msgId) {
    UserCollectionDomain userCollectionDomain = authPort.getUserCollectionById(userId);
    userCollectionDomain.getUserGroupInfoList().stream()
            .filter(userGroup -> userGroup.getGroupId().equals(groupId))
            .findFirst()
            .ifPresent(userGroup -> {
              userGroup.setLastMsgId(msgId);
              groupPort.saveUser(userCollectionDomain);
            });
  }

  private ChatMessageResponse processMessageOperation(String messageId, EMessageType operationType) {
    User currentUser = authPort.getUserAuth();
    Message message = getValidatedMessage(messageId);
    Group group = validateOperationMessage(currentUser, message, true);

    EMessageType newMessageType = determineNewMessageType(message, operationType);
    updateMessage(message, newMessageType);

    notifyGroupMembers(message, group);

    return chatMapper.messageToMessageResponse(message, userMapper.userToUserBasicDto(currentUser), group.getGroupType());
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

    if (!(mine == isMessageAuthor) || !isGroupMember) {
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

  private EReactionType stringToReactionType(String reactionType) {
    try {
      return EReactionType.valueOf(reactionType.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      return EReactionType.LIKE;
    }
  }

}