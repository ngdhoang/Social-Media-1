package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;

public class WebsocketContextHolder {
  private static final ThreadLocal<UserBasicDto> contextHolder = new ThreadLocal<>();

  public static void setContext(UserBasicDto userWsDetails) {
    contextHolder.set(userWsDetails);
  }

  public static UserBasicDto getContext() {
    return contextHolder.get();
  }

  public static void clearContext() {
    contextHolder.remove();
  }
}
