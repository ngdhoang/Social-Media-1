package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequence;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageSequenceRepository extends MongoRepository<ImageSequence, String> {
  @Query("{ 'postId' : ?0 }")
  Optional<ImageSequence> findByPostId(String postId);
}
