package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.post.PostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.post.ReactionPostRepository;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionPostImpl implements ReactionPostPort {
    private final ReactionPostRepository reactionPostRepository;
    private final PostRepository postRepository;

    @Override
    public ReactionPost saveReactionPost(ReactionPost reactionPost) {
        return reactionPostRepository.save(reactionPost);
    }


    @Override
    public Boolean deleteReactionPostById(Long id) {
        if (findReactionPostById(id) == null) {
            throw new CustomException("Reaction not found", HttpStatus.NOT_FOUND);
        }

        reactionPostRepository.deleteById(id);
        return false;
    }

    @Override
    public List<ReactionPost> findAllReactionPostByPost(Post post) {
        return reactionPostRepository.findAllByPost(post);
    }

    @Override
    public ReactionPost findReactionPostByIdAndPost(Long id, Post post) {
        ReactionPost reactionPost = reactionPostRepository.findById(id).orElse(null);
        if (reactionPost == null || reactionPost.getPost().getPostId() != post.getPostId()) return null;
        return reactionPost;
    }

//    @Override
//    public List<ReactionPost> findAllReactionPostByPost(Long id) {
//        Post post = postRepository.findById(id).orElseThrow(
//                () ->{throw new CustomException("Post not found",HttpStatus.NOT_FOUND);}
//        );
//        return reactionPostRepository.findAllByPost(post);
//    }

//    @Override
//    public ReactionPost findReactionPostByIdAndPost(Long d, Post post) {
//        return reactionPostRepository.findByReactionPostIdAndAndPost(id,post).orElse(null);
//    }

    @Override
    public ReactionPost findReactionPostById(Long id) {
        return reactionPostRepository.findById(id).orElse(null);
    }
}
