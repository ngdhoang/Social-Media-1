package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.requests.GetCommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ActivityInteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.InteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;

import java.util.List;

public interface CommentPostInput {
  CommentResponse createCommentRoot(CommentRequest comment);

  CommentResponse createCommentChild(Long commentIdSrc, CommentRequest comment);

  List<CommentResponse> getCommentsByPostId(Long postId, GetCommentRequest getCommentRequest);

    List<ActivityInteractionResponse> getListCommentInteractions(GetCommentRequest getCommentRequest);

    CommentResponse getCommentById(Long commentId);

  List<CommentResponse> getCommentChildByParentId(Long id, GetCommentRequest getCommentRequest);

  MessageResponse deleteComment(Long commentId);

  CommentResponse updateComment(Long commentId, CommentRequest comment);

    List<InteractionResponse> getCommentsByInteractions(GetCommentRequest getCommentRequest);

//  List<InteractionResponse> getCommentsByInteractions();
}
