package com.GHTK.Social_Network.infrastructure.payload.requests;

import lombok.Data;

@Data
public class PaginationRequest {

    private int skip;

    private int limit;

    private String sortBy;

    private String orderBy;
}
