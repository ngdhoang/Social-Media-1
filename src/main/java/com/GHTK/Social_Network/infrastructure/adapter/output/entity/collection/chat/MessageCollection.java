package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
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

  private ReactionMessagesCollection reactionMsgs;

  @AllArgsConstructor
  @Getter
  class ReactionMessagesCollection {
    private List<Long> likeIds;
    private List<Long> smileIds;
    private List<Long> argyIds;
    private List<Long> loveIds;
  }

  private Long reactionQuantity;

  @CreatedDate
  private Date createAt;
}
