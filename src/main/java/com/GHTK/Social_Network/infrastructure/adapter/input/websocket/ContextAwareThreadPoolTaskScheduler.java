package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

@Component
public class ContextAwareThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {
  @Override
  public void execute(Runnable task) {
    super.execute(createContextAwareRunnable(task));
  }

  @Override
  public Future<?> submit(Runnable task) {
    return super.submit(createContextAwareRunnable(task));
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    return super.submit(createContextAwareCallable(task));
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable task, java.util.Date startTime) {
    return super.schedule(createContextAwareRunnable(task), startTime);
  }

  private Runnable createContextAwareRunnable(Runnable task) {
    UserBasicDto context = WebsocketContextHolder.getContext();
    return () -> {
      UserBasicDto originalContext = WebsocketContextHolder.getContext();
      try {
        if (context != null) {
          WebsocketContextHolder.setContext(context);
        }
        task.run();
      } finally {
        if (context != null) {
          WebsocketContextHolder.setContext(originalContext);
        } else {
          WebsocketContextHolder.clearContext();
        }
      }
    };
  }

  private <T> Callable<T> createContextAwareCallable(Callable<T> task) {
    UserBasicDto context = WebsocketContextHolder.getContext();
    return () -> {
      UserBasicDto originalContext = WebsocketContextHolder.getContext();
      try {
        if (context != null) {
          WebsocketContextHolder.setContext(context);
        }
        return task.call();
      } finally {
        if (context != null) {
          WebsocketContextHolder.setContext(originalContext);
        } else {
          WebsocketContextHolder.clearContext();
        }
      }
    };
  }
}