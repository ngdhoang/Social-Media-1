package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.domain.UserWsDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketContextHolder {
  private static final ThreadLocal<UserWsDetails> contextHolder = new ThreadLocal<>();
  private static final Logger log = LoggerFactory.getLogger(WebsocketContextHolder.class);

  public static void setContext(UserWsDetails userWsDetails) {
    log.debug("Setting context: {} for thread: {}", userWsDetails, Thread.currentThread().getName());
    contextHolder.set(userWsDetails);
  }

  public static UserWsDetails getContext() {
    UserWsDetails details = contextHolder.get();
    log.debug("Getting context: {} for thread: {}", details, Thread.currentThread().getName());
    return details;
  }

  public static void clearContext() {
    log.debug("Clearing context for thread: {}", Thread.currentThread().getName());
    contextHolder.remove();
  }
}