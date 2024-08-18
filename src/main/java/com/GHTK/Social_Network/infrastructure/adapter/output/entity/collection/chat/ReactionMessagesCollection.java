package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionMessagesCollection {
  private EReactionTypeEntity reactionType;

  private Long userId;
}
