package com.GHTK.Social_Network.infrastructure.adapter.output.repository.node;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.HometownNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HometownNodeRepository extends Neo4jRepository<HometownNode, Integer>{

    @Query("MATCH (hometownNode:`Hometown`) " +
            "RETURN hometownNode{" +
            "    .id, " +
            "    **nodeLabels**: labels(hometownNode), " +
            "    **elementId**: elementId(hometownNode)" +
            "}")
    List<HometownNode> getAllTest();
}
