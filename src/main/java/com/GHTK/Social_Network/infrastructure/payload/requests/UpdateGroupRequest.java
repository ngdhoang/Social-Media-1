package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupRequest {
    private String id;

    private String groupName;

    private List<Long> members;
}
