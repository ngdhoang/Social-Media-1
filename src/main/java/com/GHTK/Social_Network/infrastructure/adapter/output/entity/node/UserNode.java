package com.GHTK.Social_Network.infrastructure.adapter.output.entity.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node("User")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNode {
    @Id
    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    @Relationship(type = "RELATED_TO")
    private Set<UserRelationship> userRelationships;

    @Relationship(type = "BLOCK_TO", direction = Relationship.Direction.OUTGOING)
    private Set<UserRelationship> blockTo;

    @Relationship(type = "LIVES_IN", direction = Relationship.Direction.OUTGOING)
    private HometownNode hometownNode;

    public UserNode(Long userId, String firstName, String lastName, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
