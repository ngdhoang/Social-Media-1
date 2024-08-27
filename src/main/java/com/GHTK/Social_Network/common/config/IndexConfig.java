package com.GHTK.Social_Network.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class IndexConfig {
    private final Driver neo4jDriver;

    @PostConstruct
    public void createFullTextIndex() {
        try (var session = neo4jDriver.session()) {
            String checkIndexQuery = "SHOW INDEXES WHERE name = 'userIndex'";
            var result = session.run(checkIndexQuery);
            if (!result.hasNext()) {
                String createIndexQuery = "CREATE FULLTEXT INDEX userIndex FOR (n:User) ON EACH [n.fullName, n.email]";
                session.run(createIndexQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}