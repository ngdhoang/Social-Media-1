package com.GHTK.Social_Network.application.port.input;


import com.GHTK.Social_Network.infrastructure.payload.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostPortInput {
        Page<PostDto> findAllPostByUserId(Long userId,Pageable pageable);

        void createPost (PostDto postDto);

        void updatePostByPostId(Long postId,PostDto postDto);

        void deletePostByPostId(Long postId);

//        void deleteGroupPostByIds(List<Long> ids);
}
