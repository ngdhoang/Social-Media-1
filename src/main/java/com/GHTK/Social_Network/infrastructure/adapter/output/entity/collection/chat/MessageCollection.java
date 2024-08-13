package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageCollection {
  @Id
  private String msgId;

  private String groupId;

  private Long userAuthId;

  private Long replyMsgId;

  private EMessageTypeCollection msgType;

  private String content;

  private List<Long> tags;

  private List<String> images;

  private List<ReactionMessagesCollection> reactionMsgs;

  private Long reactionQuantity;

  private Instant createAt;

  public MessageCollection(String groupId, Long userAuthId, Long replyMsgId, EMessageTypeCollection msgType, String content, List<Long> tags, List<String> images, List<ReactionMessagesCollection> reactionMsgs, Long reactionQuantity, Instant createAt) {
    this.groupId = groupId;
    this.userAuthId = userAuthId;
    this.replyMsgId = replyMsgId;
    this.msgType = msgType;
    this.content = content;
    this.tags = tags == null ? new LinkedList<>() : tags;
    this.images = images == null ? new LinkedList<>() : images;
    this.reactionMsgs = new LinkedList<>();
    this.reactionQuantity = 0L;
    this.createAt = createAt;
  }
}
