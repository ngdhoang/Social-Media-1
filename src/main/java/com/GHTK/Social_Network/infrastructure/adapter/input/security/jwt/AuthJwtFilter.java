package com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt;

import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsServiceImpl;
import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.AuthRepositoryPortImpl;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthJwtFilter extends OncePerRequestFilter {
  private final JwtUtils jwtUtils;

  private final UserDetailsServiceImpl userDetailsService;

  private final AuthRepositoryPortImpl tokenRepository;

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);
    if (jwt.isEmpty()) {
      sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Empty token");
      return;
    }

    try {
      final String userEmail = jwtUtils.extractUserEmail(jwt);
      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        var tokenOptional = tokenRepository.findByToken(jwt);

        if (tokenOptional.isPresent()) {
          var token = tokenOptional.get();
          if (token.isExpired() || token.isRevoked()) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token has expired or revoked");
            return;
          }
          if (jwtUtils.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          } else {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token");
            return;
          }
        } else {
          sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token");
          return;
        }
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: " + e);
      sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the token");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String errorMessage) throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ResponseEntity<Object> responseEntity = ResponseHandler.generateErrorResponse(errorMessage, status);
    String jsonResponse = objectMapper.writeValueAsString(responseEntity.getBody());
    response.getWriter().write(jsonResponse);
  }
}