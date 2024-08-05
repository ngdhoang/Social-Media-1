package com.GHTK.Social_Network.domain.entity;

import com.GHTK.Social_Network.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  @Column(columnDefinition = "TEXT")
  private String content;

  private LocalDate createdAt;

  @Enumerated(EnumType.STRING)
  private EPostStatus postStatus;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<Comment> comments;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionPost> reactionPosts;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImagePost> imagePosts;

}
