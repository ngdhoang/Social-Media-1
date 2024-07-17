package com.GHTK.Social_Network.application.port.input;


import com.GHTK.Social_Network.infrastructure.payload.dto.PostDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostPortInput {
        Page<PostDto> findAllPostByUserId(Long userId,Pageable pageable);

        PostDto createPost (PostCreateRequest postCreateRequest);

        void updatePostByPostId(Long postId,PostDto postDto);

        void deletePostByPostId(Long postId);

//        void deleteGroupPostByIds(List<Long> ids);
}
