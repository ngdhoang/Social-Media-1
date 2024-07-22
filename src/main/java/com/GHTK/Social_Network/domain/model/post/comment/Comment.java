package com.GHTK.Social_Network.domain.model.post.comment;

import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
  private Long commentId;

  private Date createUp;

  private Boolean isDelete = false;

  private String content;

  private Comment parentComment;

  private List<Comment> childComments = new ArrayList<>();

  private User user;

  private Post post;

  private List<ImageComment> imageComments;

  public Comment(Date createUp, String content, User user, Post post) {
    this.createUp = createUp;
    this.content = content;
    this.user = user;
    this.post = post;
  }

  public void addChildComment(Comment childComment) {
    childComments.add(childComment);
    childComment.setParentComment(this);
  }

}
