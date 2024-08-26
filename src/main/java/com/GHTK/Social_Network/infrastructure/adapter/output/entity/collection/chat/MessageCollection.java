package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageCollection {
  @Id
  private ObjectId id;

  private String groupId;

  private Long userAuthId;

  private String replyMsgId;

  private EMessageTypeCollection msgType;

  private String content;

  private List<Long> tags;

  private List<String> images;

  private List<ReactionMessagesCollection> reactionMsgs;

  private Long reactionQuantity;

  private Instant createAt;
}