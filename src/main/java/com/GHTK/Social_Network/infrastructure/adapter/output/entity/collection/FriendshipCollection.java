package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendshipCollection {
    private String userId;
    private LinkedList<Long> listFriendId;
    private LinkedList<Long> listBlockId;
}
