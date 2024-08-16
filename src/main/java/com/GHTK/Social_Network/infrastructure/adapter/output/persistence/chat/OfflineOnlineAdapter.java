package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.OfflineOnlinePort;
import com.GHTK.Social_Network.application.port.output.RedisSessionWsPort;
import com.GHTK.Social_Network.domain.UserWsDetails;
import com.GHTK.Social_Network.infrastructure.payload.dto.SessionWsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfflineOnlineAdapter implements OfflineOnlinePort {
  private final RedisSessionWsPort redisSessionWsPort;

  @Override
  public void updateOrCreateSessionInRedis(Long userId, UserWsDetails sessionWsDto) {
    String userIdStr = String.valueOf(userId);
    if (redisSessionWsPort.existsByKey(userIdStr)) {
      SessionWsDto sessionInfo = redisSessionWsPort.findByKey(userIdStr);
      List<UserWsDetails> listSession = sessionInfo.getSessions();
      listSession.add(sessionWsDto);
      sessionInfo.setSessions(listSession);
      redisSessionWsPort.createOrUpdate(userIdStr, sessionInfo);
    } else {
      SessionWsDto sessionInfo = new SessionWsDto(Collections.singletonList(sessionWsDto));
      redisSessionWsPort.createOrUpdate(userIdStr, sessionInfo);
    }
  }

  @Override
  public void removeSessionInRedis(Long userId, String sessionId) {
    String userIdStr = String.valueOf(userId);
    SessionWsDto sessionInfo = redisSessionWsPort.findByKey(userIdStr);
    List<UserWsDetails> listSession = sessionInfo.getSessions();

    if (listSession.size() == 1) {
      redisSessionWsPort.deleteByKey(userIdStr);
    } else {
      listSession.removeIf(s -> s.getSession().equals(sessionId));
      redisSessionWsPort.createOrUpdate(userIdStr, sessionInfo);
    }
  }


  @Override
  public SessionWsDto getSessionInRedisByKey(Long userId, String sessionId) {
    String userIdStr = String.valueOf(userId);
    SessionWsDto sessionInfo = redisSessionWsPort.findByKey(userIdStr);

    if (sessionInfo == null) {
      return null;
    }

    List<UserWsDetails> listSession = sessionInfo.getSessions();
    boolean sessionExists = listSession.stream()
            .anyMatch(pair -> pair.getSession().equals(sessionId));

    if (sessionExists) {
      return sessionInfo;
    } else {
      return null;
    }
  }

}
