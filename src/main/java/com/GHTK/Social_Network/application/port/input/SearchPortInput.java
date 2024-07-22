package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.SearchDto;

import java.util.List;

public interface SearchPortInput {
  List<SearchDto> searchPublic(String keyword, Integer scope);
}
