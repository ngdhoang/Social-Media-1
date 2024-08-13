package com.GHTK.Social_Network.domain.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ReactionMessages {
  private List<Long> likeIds;

  private List<Long> smileIds;

  private List<Long> argyIds;

  private List<Long> loveIds;
}