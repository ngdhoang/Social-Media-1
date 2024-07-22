package com.GHTK.Social_Network.infrastructure.adapter.output;

import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.domain.repository.TokenRepository;
import com.GHTK.Social_Network.infrastructure.MapperEntity.TokenMapper;
import com.GHTK.Social_Network.infrastructure.repository.TokenEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenRepositoryAdapter implements TokenRepository {
  private final TokenEntityRepository tokenRepository;

  @Override
  public List<Token> findAllValidTokenByUser(Long id) {
    return tokenRepository.findAllValidTokenByUser(id).stream().map(
            TokenMapper.INSTANCE::toDomain
    ).toList();
  }

  @Override
  public Optional<Token> findByToken(String jwt) {
    return Optional.ofNullable(
            TokenMapper.INSTANCE.toDomain(tokenRepository.findByToken(jwt).orElse(null))
    );
  }

  @Override
  public void save(Token token) {
    tokenRepository.save(TokenMapper.INSTANCE.toEntity(token));
  }

  @Override
  public void saveAll(List<Token> tokens) {
    tokens.forEach(token -> tokenRepository.save(TokenMapper.INSTANCE.toEntity(token)));
  }
}
