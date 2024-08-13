package com.GHTK.Social_Network.infrastructure.adapter.output.entity.node;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.EFriendshipStatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRelationship {
    @RelationshipId
    private Long id;

    @TargetNode
    private UserNode user;

    private EFriendshipStatusEntity relationship;
}
