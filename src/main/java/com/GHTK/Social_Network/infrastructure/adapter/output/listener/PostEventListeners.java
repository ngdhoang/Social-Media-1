package com.GHTK.Social_Network.infrastructure.adapter.output.listener;

import com.GHTK.Social_Network.domain.event.post.PostCreateEvent;
import com.GHTK.Social_Network.domain.event.post.PostUpdateEvent;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.PostNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.CommentNodeRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.PostNodeRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.mapper.EFriendShipStatusMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.EPostStatusMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.PostMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostEventListeners {
    private final FriendCollectionRepository friendCollectionRepository;

    private final EFriendShipStatusMapperETD eFriendShipStatusMapperETD;

    private final UserNodeRepository userNodeRepository;

    private final PostNodeRepository postNodeRepository;

    private final CommentNodeRepository commentNodeRepository;

    private final EPostStatusMapperETD ePostStatusMapperETD;

    private final PostMapperETD postMapperETD;

    @EventListener
    public void handleAddPost(PostCreateEvent event) {
        Post post = event.getPost();

        Long postId = post.getPostId();
        Long userId = post.getUserId();
        UserNode userNode = userNodeRepository.getUserNodeById(userId);
        UserCollection userCollection = friendCollectionRepository.findByUserId(userId);
        List<Long> listFriendId = userCollection.getListFriendId();

        LocalDateTime createAt = LocalDateTime.now(ZonedDateTime.now().getZone());
        createPostNode(post, createAt);

        userNodeRepository.createOrUpdateUserPostRelationship(userId, postId, userNode.getMaxScorePost() + 1, createAt);

        listFriendId.stream().forEach(friendId -> {
            userNodeRepository.createOrUpdateUserPostRelationship(friendId, postId, userNode.getMaxScorePost() + 1, createAt);
        });
    }

    @EventListener
    public void handleUpdatePost(PostUpdateEvent event) {
        Post post = event.getPost();
        Long postId = post.getPostId();
        postNodeRepository.updateStatus(postId, ePostStatusMapperETD.toEntity(post.getPostStatus()));
    }

    private void createPostNode(Post post, LocalDateTime createAt) {
        PostNode postNode = postMapperETD.postDomainToNode(post);
        postNode.setCreateAt(createAt);
        postNode.setLastUpdateAt(createAt);
        postNode.setOwnerId(post.getUserId());
        postNodeRepository.save(postNode);
        postNodeRepository.createPostNodeByUserIdAndPostId(post.getPostId(), post.getUserId());
    }
}
