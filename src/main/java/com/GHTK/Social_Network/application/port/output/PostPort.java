package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.Post;
import com.GHTK.Social_Network.infrastructure.payload.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostPort {
    Page<Post> getAllPostByUserId(Long userId,Pageable pageable);

    void savePost (Post post);

    void updatePostByPostId(Post post);

    void deletePostByPostId(Long postId);

//    void deleteGroupPostByIds(List<Long> ids);
}
