package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ReactionPostInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.domain.entity.post.EReactionType;
import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.ReactionPost;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionPostMapper;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionPostService implements ReactionPostInput {
  private final ReactionPostPort reactionPostPort;
  private final PostPort postPort;
  private final AuthPort authenticationRepositoryPort;
  private final FriendShipPort friendShipPort;

  private User getUserAuth() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken) {
      return User.builder().userId(0L).build();
    }

    Object principal = authentication.getPrincipal();
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

  private void validatePostAccess(Post post) {
    if (post == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    User postOwner = post.getUser();
    User currentUser = getUserAuth();

    if (!postOwner.getIsProfilePublic()) {
      throw new CustomException("Post not accessible", HttpStatus.FORBIDDEN);
    }

    if (friendShipPort.isBlock(postOwner.getUserId(), currentUser.getUserId())) {
      throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public ReactionPostResponse handleReactionPost(Long postId, String reactionType) {
    Post post = postPort.findPostByPostId(postId);
    validatePostAccess(post);

    EReactionType newReactionType;
    try {
      newReactionType = EReactionType.valueOf(reactionType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new CustomException("Invalid reaction type", HttpStatus.BAD_REQUEST);
    }

    ReactionPost reactionPost = reactionPostPort.findByPostIdAndUserID(postId, getUserAuth().getUserId());

    if (reactionPost == null) {
      ReactionPost newReactionPost = new ReactionPost(post, getUserAuth(), newReactionType);
      return ReactionPostMapper.INSTANCE.toReactionPostResponse(reactionPostPort.saveReaction(newReactionPost));
    } else {
      if (reactionPost.getReactionType() == newReactionType) {
        reactionPostPort.deleteReaction(reactionPost);
        return null;
      } else {
        reactionPost.setReactionType(newReactionType);
        return ReactionPostMapper.INSTANCE.toReactionPostResponse(reactionPostPort.saveReaction(reactionPost));
      }
    }
  }

  @Override
  public List<ReactionPostResponse> getAllReactionInPost(Long postId) {
    return reactionPostPort.findByPostId(postId).stream().map(
            ReactionPostMapper.INSTANCE::toReactionPostResponse
    ).toList();
  }


}
