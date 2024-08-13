package com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequenceCollection;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageSequenceRepository extends MongoRepository<ImageSequenceCollection, String> {
  @Query("{ 'postId' : ?0 }")
  Optional<ImageSequenceCollection> findByPostId(String postId);
}
