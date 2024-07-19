package com.GHTK.Social_Network.domain.entity.user;

import com.GHTK.Social_Network.domain.entity.FriendShip;
import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.ReactionPost;
import com.GHTK.Social_Network.domain.entity.post.TagUser;
import com.GHTK.Social_Network.domain.entity.post.comment.Comment;
import com.GHTK.Social_Network.domain.entity.post.comment.ImageComment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Table(name = "user")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String firstName;

  private String lastName;

  private String userEmail;

  private String password;

  private String oldPassword;

  private String avatar;

  private LocalDate dob;

  private String phoneNumber;

  private String homeTown;

  private String schoolName;

  private String workPlace;

  private Boolean isProfilePublic = true;

  @Enumerated(EnumType.STRING)
  private EStatusUser statusUser;

  @Enumerated(EnumType.STRING)
  private ERole role;

//  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
//          cascade = CascadeType.ALL)
//  private List<Devices> devicesList;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<Token> tokens;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<Post> posts;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TagUser> tagUsers;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<FriendShip> friendShips;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionPost> reactionPosts;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<Comment> comments;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImageComment> imageComments;

  public User(String firstName, String lastName, String userEmail, String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.userEmail = userEmail;
    this.password = password;
  }
}
