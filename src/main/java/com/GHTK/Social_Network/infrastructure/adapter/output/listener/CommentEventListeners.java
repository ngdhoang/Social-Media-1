package com.GHTK.Social_Network.infrastructure.adapter.output.listener;

import com.GHTK.Social_Network.domain.event.comment.CommentCreateEvent;
import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.PostNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserPostRelationship;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.PostNodeRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CommentEventListeners {
    private final UserCollectionRepository userCollectionRepository;

    private final UserNodeRepository userNodeRepository;

    private final PostNodeRepository postNodeRepository;

    private final Integer maxMiles = 10;

    @EventListener
    public void handleAddCommentEvent(CommentCreateEvent event) {
        Comment comment = event.getComment();
        Long postId = comment.getPostId();
        Long userId = comment.getUserId();

        UserNode userNode = userNodeRepository.getUserNodeById(userId);
        PostNode postNode = postNodeRepository.getPostNodeByPostId(postId);
        UserCollection userCollection = userCollectionRepository.findByUserId(userId);
        UserCollection ownerPostCollection = userCollectionRepository.findByUserId(postNode.getOwnerId());
        Long ownerPostId = postNode.getOwnerId();

        LocalDateTime createAt = LocalDateTime.now(ZoneId.systemDefault());

        handleCreateRelationship(userCollection, ownerPostCollection, userNode, postId, ownerPostId, createAt);
    }


    private void handleCreateRelationship(UserCollection userCollection, UserCollection ownerPostCollection, UserNode userNode, Long postId, Long ownerPostId, LocalDateTime createdAt) {
        Set<Long> unionList = Stream.concat(
                        userCollection.getListFriendId().stream(),
                        ownerPostCollection.getListFriendId().stream())
                .collect(Collectors.toSet());
        unionList.add(userNode.getUserId());
        unionList.add(ownerPostId);

        handleUserPostRelationship(unionList, createdAt, postId);
    }

    private void handleUserPostRelationship(Set<Long> userIds, LocalDateTime createdAt, Long postId) {
        userIds.stream().forEach(friendId -> {
            UserNode userNode = userNodeRepository.getUserNodeById(friendId);
            UserPostRelationship userPostRelationship = postNodeRepository.getUserRelationshipByUserIdAndPostId(friendId, postId);
            handleRelationshipNode(userPostRelationship, friendId, postId, userNode, createdAt);
        });
    }

    private void handleRelationshipNode(UserPostRelationship userPostRelationship, Long friendId, Long postId, UserNode userNode, LocalDateTime createdAt) {
        if (userPostRelationship != null && userPostRelationship.getId() != null) {
            Duration duration = Duration.between(userPostRelationship.getUpdatedAt(), createdAt);
            if (duration.toMillis() > maxMiles) {
                createOrUpdateUserPostRelationship(friendId, postId, userNode.getMaxScorePost() + 1, createdAt);
            }
        } else {
            createOrUpdateUserPostRelationship(friendId, postId, userNode.getMaxScorePost() + 1, createdAt);
        }
    }

    private void createOrUpdateUserPostRelationship(Long userId, Long postId, Integer score, LocalDateTime updatedAt) {
        userNodeRepository.createOrUpdateUserPostRelationship(userId, postId, score, updatedAt);
    }
}
