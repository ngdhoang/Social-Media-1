package com.GHTK.Social_Network.domain.repository;

import com.GHTK.Social_Network.domain.model.user.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {
  List<Token> findAllValidTokenByUser(Long id);

  Optional<Token> findByToken(String jwt);

  void save(Token token);

  void saveAll(List<Token> tokens);
}
