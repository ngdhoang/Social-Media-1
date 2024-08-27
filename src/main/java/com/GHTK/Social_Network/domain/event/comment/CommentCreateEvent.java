package com.GHTK.Social_Network.domain.event.comment;

import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCreateEvent {
    private Comment comment;
}
