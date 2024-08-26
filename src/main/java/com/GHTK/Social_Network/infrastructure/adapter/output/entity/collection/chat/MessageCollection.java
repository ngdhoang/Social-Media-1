package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@CompoundIndexes({
        @CompoundIndex(name = "idx_group_user", def = "{'groupId': 1, 'userAuthId': 1}"),
        @CompoundIndex(name = "idx_replyMsgId", def = "{'replyMsgId': 1}"),
        @CompoundIndex(name = "idx_msgType", def = "{'msgType': 1}"),
})
public class MessageCollection {
  @Id
  private String id;

  @Indexed
  private String groupId;

  @Indexed
  private Long userAuthId;

  private String replyMsgId;

  @Indexed
  private EMessageTypeCollection msgType;

  private String content;

  private List<Long> tags;

  private List<String> images;

  private List<ReactionMessagesCollection> reactionMsgs;

  private Long reactionQuantity;

  private Instant createAt;
}