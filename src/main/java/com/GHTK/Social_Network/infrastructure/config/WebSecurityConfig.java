package com.GHTK.Social_Network.infrastructure.config;

import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.input.security.jwt.AuthEntryPointJwt;
import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.input.security.jwt.AuthenticationJwtFilter;
import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.input.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
  private final AuthEntryPointJwt unAuthorizationHandler;

  private final UserDetailsServiceImpl userDetailsService;

  private final AuthenticationJwtFilter authenticationJwtFilter;

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
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
            )
            .authenticationProvider(daoAuthenticationProvider())
            .addFilterBefore(authenticationJwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
