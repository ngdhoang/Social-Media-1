package com.GHTK.Social_Network.domain.model.post.comment;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import jakarta.persistence.*;

public class ReactionComment {
  private Long reactionCommentId;

  private EReactionType reactionType;
}
