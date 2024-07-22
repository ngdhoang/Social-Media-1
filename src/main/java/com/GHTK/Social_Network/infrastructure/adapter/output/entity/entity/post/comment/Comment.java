package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  private Date createUp;

  private Boolean isDelete = false;

  @Column(columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_comment_id")
  private Comment parentComment;

  @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Comment> childComments = new ArrayList<>();
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImageComment> imageComments;

  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionComment> reactionComments;

  public Comment(Date createUp, String content, UserEntity userEntity, Post post) {
    this.createUp = createUp;
    this.content = content;
    this.userEntity = userEntity;
    this.post = post;
  }

  public void addChildComment(Comment childComment) {
    childComments.add(childComment);
    childComment.setParentComment(this);
  }

}
