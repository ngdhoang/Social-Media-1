package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  private Date createdAt;

  private Date updateAt;

  @Enumerated(EnumType.STRING)
  private EPostStatusEntity postStatus;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImagePostEntity> imagePostEntities;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TagUserEntity> tagUserEntities;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionPostEntity> reactionPostEntities;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<CommentEntity> commentEntities;
}
