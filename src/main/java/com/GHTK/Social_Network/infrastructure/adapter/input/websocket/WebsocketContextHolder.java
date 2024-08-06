package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.domain.UserWsDetails;

public class WebsocketContextHolder {
  private static final ThreadLocal<UserWsDetails> contextHolder = new ThreadLocal<>();

  public static void setContext(UserWsDetails userWsDetails) {
    contextHolder.set(userWsDetails);
  }

  public static UserWsDetails getContext() {
    return contextHolder.get();
  }

  public static void clearContext() {
    contextHolder.remove();
  }
}
