package com.GHTK.Social_Network.common.config;

import com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt.AuthEntryPointJwt;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt.AuthJwtFilter;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
  private final AuthEntryPointJwt unAuthorizationHandler;

  private final UserDetailsServiceImpl userDetailsService;

  private final AuthJwtFilter authJwtFilter;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
    return auth.getAuthenticationManager();
  }

  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unAuthorizationHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                    .requestMatchers("/api/auth/**", "/api/search", "/api/post/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/profile").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/posts/{postId}/comments",
                            "/api/posts/comments/{commentId}",
                            "/api/posts/comments/{commentId}/replies",
                            "/api/post")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/post/{p}/reaction").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/post", "/api/post/{id}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/post/{id}/comment").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/post/{id}/comment").permitAll()
                    .requestMatchers(HttpMethod.GET, "api/reaction_post/{p}").permitAll()
                    .anyRequest().authenticated()
            )
            .authenticationProvider(daoAuthenticationProvider())
            .addFilterBefore(authJwtFilter, UsernamePasswordAuthenticationFilter.class);
//            .oauth2Login(
//										login -> login
//														.defaultSuccessUrl("/home")
//														.failureUrl("/authentication?error=true")
//						);
    return http.build();
  }
}
