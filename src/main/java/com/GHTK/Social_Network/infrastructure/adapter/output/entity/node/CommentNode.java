package com.GHTK.Social_Network.infrastructure.adapter.output.entity.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;

@Node("Comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentNode {
    @Id
    private Long id;

    private Long commentId;

    private LocalDateTime createAt;
    

    @Relationship(type = "COMMENT_TO", direction = Relationship.Direction.OUTGOING)
    private PostNode postNode;

    public CommentNode(Long commentId, LocalDateTime createAt) {
        this.commentId = commentId;
        this.createAt = createAt;
    }
}
