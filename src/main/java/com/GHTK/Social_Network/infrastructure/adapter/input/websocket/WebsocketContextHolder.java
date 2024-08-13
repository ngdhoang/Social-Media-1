package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;

public class WebsocketContextHolder {
  private static final InheritableThreadLocal<UserBasicDto> contextHolder = new InheritableThreadLocal<>();

  public static void setContext(UserBasicDto context) {
    contextHolder.set(context);
  }

  public static UserBasicDto getContext() {
    return contextHolder.get();
  }

  public static void clearContext() {
    contextHolder.remove();
  }
}
