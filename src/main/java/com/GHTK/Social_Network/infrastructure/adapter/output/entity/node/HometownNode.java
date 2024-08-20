package com.GHTK.Social_Network.infrastructure.adapter.output.entity.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Hometown")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HometownNode {
    @Id
    private Integer hometownId;
}
