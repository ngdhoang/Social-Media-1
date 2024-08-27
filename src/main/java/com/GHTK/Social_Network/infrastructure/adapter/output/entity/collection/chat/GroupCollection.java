package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@CompoundIndexes({
        @CompoundIndex(name = "idx_groupName", def = "{'groupName': 1}")
})
public class GroupCollection {
  @Id
  private ObjectId id;

  private String groupBackground;

  private String groupName;

  private EGroupTypeCollection groupType;

  private List<MemberCollection> members;

  private List<String> msgPin;

  @CreatedDate
  private Date createAt;
}