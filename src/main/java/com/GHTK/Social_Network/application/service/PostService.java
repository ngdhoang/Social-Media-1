package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.PostPortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.PostPort;
import com.GHTK.Social_Network.domain.entity.Post;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.PostMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.PostDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService implements PostPortInput {

    private final PostPort postPort;

    private final RedisTemplate<String, PostDto> postDtoRedisTemplate;

    private final AuthPort authenticationRepositoryPort;

    private User getUserAuth() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        return authenticationRepositoryPort.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
    }
    @Override
    public Page<PostDto> findAllPostByUserId(Long userId,Pageable pageable) {
        // get post
//        if(userId != getUserAuth().getUserId() && post private){
//            throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
//
//        if (  post friend && checkFriend ){
//
//            }
        return null;
    }

    @Override
    public PostDto createPost(PostCreateRequest postCreateRequest) {
        PostDto postDto = PostMapper.INSTANCE.postDtoToPostCreateRequest(postCreateRequest);
        Post post = postPort.savePost(postDto);
        return PostMapper.INSTANCE.postToPostDto(post);
    }

//    @Override
//    public PostDto createPost(PostDto postDto) {
//        PostCreateRequest postCreateRequest = PostMapper.INSTANCE.postDtoToPost(postDto);
//       return postPort.savePost(post);
//
//    }

    @Override
    public void updatePostByPostId(Long postId, PostDto postDto) {

    }

    @Override
    public void deletePostByPostId(Long postId) {

    }

//    @Override
//    public void deleteGroupPostByIds(List<Long> ids) {
//
//    }
}
