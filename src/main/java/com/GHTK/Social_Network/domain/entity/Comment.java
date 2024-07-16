package com.GHTK.Social_Network.domain.entity;

import com.GHTK.Social_Network.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter

public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  private LocalDate createUp;

  private Boolean isDelete;

  @Column(columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "postId", nullable = false)
  private Post post;

  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionComment> reactionComments;

  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImageComment> imageComments;

  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<Comment> comments;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "commentParentId", nullable = false)
  private Comment comment;

}
