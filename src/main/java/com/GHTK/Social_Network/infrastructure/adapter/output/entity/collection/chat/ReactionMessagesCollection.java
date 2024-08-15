package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ReactionMessagesCollection {
  private List<Long> likeIds;

  private List<Long> smileIds;

  private List<Long> argyIds;

  private List<Long> loveIds;
}
