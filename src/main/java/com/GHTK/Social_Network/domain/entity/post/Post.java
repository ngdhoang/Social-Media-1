package com.GHTK.Social_Network.domain.entity.post;

import com.GHTK.Social_Network.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  private Date createdAt;

  private Date updateAt;

  @Enumerated(EnumType.STRING)
  private EPostStatus postStatus;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImagePost> imagePosts;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TagUser> tagUsers;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionPost> reactionPosts;
}
