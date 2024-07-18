package com.GHTK.Social_Network.infrastructure.adapter.output.repository.post;

import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.ReactionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionPostRepository extends JpaRepository<ReactionPost, Long> {

//    Optional<ReactionPost> findByReactionPostIdAndAndPost(Long reactionPostId, Post post);

//    Optional<ReactionPost> findAllReactionPostByPostId(Long id);

    List<ReactionPost> findAllByPost(Post post);

//    ReactionPost findReactionPostById(Long reactionPostId);
}
