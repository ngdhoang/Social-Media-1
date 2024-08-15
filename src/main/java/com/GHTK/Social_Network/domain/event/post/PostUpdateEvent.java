package com.GHTK.Social_Network.domain.event.post;

import com.GHTK.Social_Network.domain.model.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostUpdateEvent {
    private Post post;
}
