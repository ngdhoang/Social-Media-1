package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ReactionPostInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.domain.entity.post.EReactionType;
import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.ReactionPost;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post.ReactionPostImpl;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.PostMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.ReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Check;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionPostService implements ReactionPostInput {
    private final ReactionPostPort reactionPostPort;
    private final AuthPort authenticationRepositoryPort;
    private final PostPort postPort;

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
    public ReactionPostResponse createReactionPost(ReactionPostRequest reactionPostRequest) {
       //Check post exists
        Post post = postPort.findPostById(reactionPostRequest.getPostId());
        if (post == null){
            throw new CustomException("Post does not exist", HttpStatus.NOT_FOUND);
        }
        //Lay user dang post
        User userPost = post.getUser();

        //Lay user cua minh
        User user = getUserAuth();

        //Check block
        //---------------------------------
        ReactionPost reactionPost = ReactionPost.builder().reactionType(EReactionType.LIKE).build();
        return PostMapper.INSTANCE.reactionPostToReactionPostResponse(reactionPost);
    }

    @Override
    public ReactionPostResponse updateReactionPost(ReactionPostRequest reactionPostRequest) {
        User user = getUserAuth();

        //Check post exists
        Post post = postPort.findPostById(reactionPostRequest.getPostId());
        if (post == null){
            throw new CustomException("Post does not exist", HttpStatus.NOT_FOUND);
        }
        if (user != post.getUser()){
            throw new CustomException("User not exist",HttpStatus.NOT_FOUND);
        }
        //Check reaction exist
        ReactionPost reactionPost = reactionPostPort.findReactionPostByIdAndPost(reactionPostRequest.getReactionId(),post);
        if (reactionPost == null){
            throw new CustomException("The react does not exist", HttpStatus.NOT_FOUND);
        }
        ReactionPost newReactionPost = reactionPostPort.saveReactionPost(reactionPost);
        //Check block
        //------------------------



        return PostMapper.INSTANCE.reactionPostToReactionPostResponse(newReactionPost);
    }

    @Override
    public MessageResponse deleteReactionPost(Long id) {
        //Check reaction exist
        ReactionPost reactionPost = reactionPostPort.findReactionPostById(id);
        if (reactionPost == null){
            throw new CustomException("The react does not exist", HttpStatus.NOT_FOUND);
        }
        if (!reactionPostPort.deleteReactionPostById(id))
            throw new CustomException("Don't have permission to delete this post", HttpStatus.FORBIDDEN);
        return MessageResponse.builder().message("Successfully deleted").build();
    }

    @Override
    public List<ReactionPostResponse> getAllReactionPostByPostId(Long postId) {
        //Check post is exist
        Post post = postPort.findPostById(postId);
        if (post == null){
            throw new CustomException("Post does not exist", HttpStatus.NOT_FOUND);
        }
        List<ReactionPost> reactionPostList = reactionPostPort.findAllReactionPostByPost(post);

        return reactionPostList.stream()
                .map(PostMapper.INSTANCE::reactionPostToReactionPostResponse)
                .collect(Collectors.toList());
    }
}
