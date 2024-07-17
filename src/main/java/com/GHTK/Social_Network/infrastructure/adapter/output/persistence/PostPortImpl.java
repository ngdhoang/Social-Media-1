package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.PostPort;
import com.GHTK.Social_Network.domain.entity.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.PostRepository;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.PostMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostPortImpl implements PostPort {
    private final PostRepository postRepository;

    @Override
    public Page<Post> getAllPostByUserId(Long userId, Pageable pageable) {
        return null;
    }

    @Override
    public Post savePost(PostDto postDto) {
        Post post = PostMapper.INSTANCE.postDtoToPost(postDto);
        return postRepository.save(post) ;
    }

    @Override
    public void updatePostByPostId(Post post) {

    }

    @Override
    public void deletePostByPostId(Long postId) {

    }
}
